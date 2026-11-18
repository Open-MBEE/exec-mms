#!/usr/bin/env python3
"""Copy the MMS indices of an Elasticsearch 7 cluster into a new Elasticsearch or OpenSearch cluster.

Each target index is created with the mapping MMS ships for it, then filled from the source.
The source cluster is only read. See docs/search-migration.rst for the operator procedure.
"""
import argparse
import base64
import hashlib
import json
import os
import ssl
import sys
import time
import urllib.error
import urllib.parse
import urllib.request

SUFFIXES = ("_node", "_commit", "_metadata")
CARRIED_SETTINGS = (
    "index.number_of_shards",
    "index.number_of_replicas",
    "index.max_result_window",
    "index.mapping.total_fields.limit",
    "index.mapping.nested_fields.limit",
    "index.mapping.depth.limit",
)
DEFAULT_MAPPINGS = os.path.join(
    os.path.dirname(os.path.abspath(__file__)), "..", "src", "main", "resources", "elastic_mappings")


class MigrationError(Exception):
    pass


class Cluster:
    def __init__(self, label, url, user, password, context, timeout):
        self.label = label
        self.url = url.rstrip("/")
        self.user = user
        self.password = password
        self.context = context
        self.timeout = timeout

    def request(self, method, path, body=None, params=None, ndjson=False, missing_ok=False):
        url = self.url + path
        if params:
            url += "?" + urllib.parse.urlencode(params)
        headers = {"Accept": "application/json"}
        data = None
        if body is not None:
            data = body if ndjson else json.dumps(body).encode("utf-8")
            headers["Content-Type"] = "application/x-ndjson" if ndjson else "application/json"
        if self.user:
            token = base64.b64encode(f"{self.user}:{self.password or ''}".encode("utf-8")).decode("ascii")
            headers["Authorization"] = "Basic " + token
        for attempt in range(6):
            req = urllib.request.Request(url, data=data, method=method, headers=headers)
            try:
                with urllib.request.urlopen(req, timeout=self.timeout, context=self.context) as response:
                    text = response.read()
                return json.loads(text) if text else {}
            except urllib.error.HTTPError as error:
                detail = error.read().decode("utf-8", "replace")
                if error.code == 404 and missing_ok:
                    return None
                # 429 and 503 mean the cluster is busy; back off and retry the same request.
                if error.code in (429, 503) and attempt < 5:
                    time.sleep(2 ** attempt)
                    continue
                raise MigrationError(f"{self.label}: {method} {path} returned {error.code}: {detail[:2000]}")
            except urllib.error.URLError as error:
                raise MigrationError(f"{self.label}: cannot reach {self.url}: {error.reason}")

    def info(self):
        root = self.request("GET", "/")
        version = root.get("version", {})
        name = "OpenSearch" if version.get("distribution") == "opensearch" else "Elasticsearch"
        return name, version.get("number", "?"), root.get("cluster_uuid")

    def exists(self, index):
        return self.request("GET", "/" + quote(index), missing_ok=True) is not None

    def count(self, index, refresh=False):
        if refresh:
            self.request("POST", "/" + quote(index) + "/_refresh")
        return self.complete(index, self.request("GET", "/" + quote(index) + "/_count"))["count"]

    def complete(self, index, response):
        """Reject an answer that left shards out; it would pass for a smaller index."""
        shards = response.get("_shards", {})
        if shards.get("failed") or shards.get("successful", 0) < shards.get("total", 0):
            raise MigrationError(f"{self.label}: {index} answered from {shards.get('successful')} of "
                                 f"{shards.get('total')} shards: {json.dumps(shards.get('failures', []))[:2000]}")
        return response

    def scan(self, index, batch_size, keep_alive):
        """Yield every hit of an index, in batches, through the scroll API."""
        page = self.request("POST", "/" + quote(index) + "/_search", {"size": batch_size, "sort": ["_doc"]},
                            {"scroll": keep_alive})
        try:
            while self.complete(index, page)["hits"]["hits"]:
                yield page["hits"]["hits"]
                page = self.request("POST", "/_search/scroll",
                                    {"scroll": keep_alive, "scroll_id": page["_scroll_id"]})
        finally:
            if page.get("_scroll_id"):
                try:
                    self.request("DELETE", "/_search/scroll", {"scroll_id": page["_scroll_id"]}, missing_ok=True)
                except MigrationError:
                    pass


def quote(value):
    return urllib.parse.quote(value, safe="")


def merge_properties(shipped, existing):
    """Keep every field MMS declares; add the fields the source index learned dynamically."""
    merged = dict(existing)
    for name, declared in shipped.items():
        learned = existing.get(name)
        if isinstance(learned, dict) and "properties" in declared and "properties" in learned:
            combined = dict(declared)
            combined["properties"] = merge_properties(declared["properties"], learned["properties"])
            merged[name] = combined
        else:
            merged[name] = declared
    return merged


def project_schema(source, metadata_index):
    if not source.exists(metadata_index):
        return None
    found = source.request("POST", "/" + quote(metadata_index) + "/_search",
                           {"size": 1, "query": {"term": {"type": "Project"}}})
    hits = found["hits"]["hits"]
    return hits[0]["_source"].get("schema") if hits else None


def shipped_mapping(source, index, mappings_dir):
    for suffix in ("_commit", "_metadata"):
        if index.endswith(suffix):
            return load_mapping(mappings_dir, suffix[1:] + ".json")
    schema = project_schema(source, index[:-len("_node")] + "_metadata") or "default"
    name = schema + "_node.json"
    if not os.path.isfile(os.path.join(mappings_dir, name)):
        print(f"  warning: no mapping for schema '{schema}', using default_node.json")
        name = "default_node.json"
    return load_mapping(mappings_dir, name)


def load_mapping(mappings_dir, name):
    with open(os.path.join(mappings_dir, name), encoding="utf-8") as handle:
        return json.load(handle)


def target_body(source, index, mappings_dir):
    described = source.request("GET", "/" + quote(index), params={"flat_settings": "true"})[index]
    mapping = shipped_mapping(source, index, mappings_dir)
    learned = described.get("mappings", {}).get("properties", {})
    mapping["properties"] = merge_properties(mapping.get("properties", {}), learned)
    settings = {key: value for key, value in described.get("settings", {}).items() if key in CARRIED_SETTINGS}
    return {"settings": settings, "mappings": mapping}


def copy_remote(source, target, index, args):
    remote = {"host": args.remote_host or source.url}
    if source.user:
        remote["username"] = source.user
        remote["password"] = source.password or ""
    body = {"source": {"remote": remote, "index": index, "size": args.batch_size}, "dest": {"index": index}}
    task = target.request("POST", "/_reindex", body, {"wait_for_completion": "false", "refresh": "true"})["task"]
    delay = 0.1
    while True:
        status = target.request("GET", "/_tasks/" + quote(task))
        if status.get("completed"):
            break
        time.sleep(delay)
        delay = min(delay * 2, 5)
    if status.get("error"):
        raise MigrationError(f"reindex of {index} failed: {json.dumps(status['error'])[:2000]}")
    failures = status.get("response", {}).get("failures") or []
    if failures:
        raise MigrationError(f"reindex of {index} reported failures: {json.dumps(failures)[:2000]}")


def copy_client(source, target, index, args):
    for hits in source.scan(index, args.batch_size, args.scroll):
        lines, size = [], 0
        for hit in hits:
            action = {"_index": index, "_id": hit["_id"]}
            if hit.get("_routing") is not None:
                action["routing"] = hit["_routing"]
            pair = json.dumps({"index": action}) + "\n" + json.dumps(hit["_source"], ensure_ascii=False) + "\n"
            pair = pair.encode("utf-8")
            # Stay well under the server's default 100 MB request limit.
            if lines and size + len(pair) > args.max_bulk_bytes:
                send_bulk(target, index, lines)
                lines, size = [], 0
            lines.append(pair)
            size += len(pair)
        if lines:
            send_bulk(target, index, lines)


def send_bulk(target, index, lines):
    result = target.request("POST", "/_bulk", b"".join(lines), ndjson=True)
    if result.get("errors"):
        failed = [item["index"] for item in result["items"] if item["index"].get("error")]
        raise MigrationError(f"bulk into {index} rejected {len(failed)} document(s): {json.dumps(failed[:3])[:2000]}")


def digest(cluster, index, args):
    """Order-independent fingerprint of every document id and source in an index."""
    total, combined = 0, 0
    for hits in cluster.scan(index, args.batch_size, args.scroll):
        for hit in hits:
            canonical = json.dumps([hit["_id"], hit["_source"]], sort_keys=True, separators=(",", ":"))
            combined ^= int.from_bytes(hashlib.sha256(canonical.encode("utf-8")).digest(), "big")
            total += 1
    return total, f"{combined:064x}"


def verify(source, target, index, args):
    if not target.exists(index):
        print(f"  MISSING on target: {index}")
        return False
    expected, actual = source.count(index), target.count(index, refresh=True)
    ok = expected == actual
    line = f"  {index}: source {expected}, target {actual}"
    if ok and args.deep_verify:
        ok = digest(source, index, args) == digest(target, index, args)
        line += ", content " + ("identical" if ok else "DIFFERS")
    print(line + ("" if ok else "  <-- MISMATCH"))
    return ok


def select_indices(source, projects):
    names = [row["index"] for row in source.request("GET", "/_cat/indices", params={"format": "json"})]
    wanted = sorted(name for name in names if not name.startswith(".") and name.endswith(SUFFIXES))
    if projects:
        allowed = {project.lower() + suffix for project in projects for suffix in SUFFIXES}
        wanted = [name for name in wanted if name in allowed]
    skipped = sorted(name for name in names if not name.startswith(".") and not name.endswith(SUFFIXES))
    return wanted, skipped


def migrate(source, target, index, args):
    if target.exists(index):
        if args.existing == "fail":
            raise MigrationError(f"{index} already exists on the target; pass --existing skip or --existing overwrite")
        if args.existing == "skip":
            print(f"  {index}: exists on target, left as is")
            return
        target.request("DELETE", "/" + quote(index))
    body = target_body(source, index, args.mappings)
    target.request("PUT", "/" + quote(index), body)
    started = time.time()
    (copy_remote if args.copy == "remote" else copy_client)(source, target, index, args)
    print(f"  {index}: copied in {time.time() - started:.1f}s")


def tls_context(args):
    if args.insecure:
        return ssl._create_unverified_context()
    if args.ca_file:
        return ssl.create_default_context(cafile=args.ca_file)
    return None


def parse_args(argv):
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--source", required=True, help="URL of the Elasticsearch 7 cluster, e.g. http://old-es:9200")
    parser.add_argument("--target", required=True, help="URL of the new Elasticsearch or OpenSearch cluster")
    parser.add_argument("--copy", choices=("remote", "client"), default="remote",
                        help="remote: the target pulls from the source with reindex-from-remote (default); "
                             "client: this script reads the source and bulk-indexes into the target")
    parser.add_argument("--remote-host", help="source URL as the target reaches it, when it differs from --source")
    parser.add_argument("--project", action="append", default=[], help="migrate only this project id (repeatable)")
    parser.add_argument("--existing", choices=("fail", "skip", "overwrite"), default="fail",
                        help="what to do with an index that already exists on the target (default: fail)")
    parser.add_argument("--mappings", default=DEFAULT_MAPPINGS, help="directory holding the MMS mapping files")
    parser.add_argument("--batch-size", type=int, default=1000, help="documents per scroll page (default: 1000)")
    parser.add_argument("--max-bulk-bytes", type=int, default=20 * 1024 * 1024,
                        help="largest bulk request sent by --copy client (default: 20 MB)")
    parser.add_argument("--scroll", default="10m", help="scroll keep-alive (default: 10m)")
    parser.add_argument("--timeout", type=int, default=300, help="HTTP timeout in seconds (default: 300)")
    parser.add_argument("--dry-run", action="store_true", help="list what would be migrated and exit")
    parser.add_argument("--verify-only", action="store_true", help="compare source and target without copying")
    parser.add_argument("--deep-verify", action="store_true",
                        help="also compare the id and source of every document, not just the counts")
    parser.add_argument("--ca-file", help="CA bundle for HTTPS connections made by this script")
    parser.add_argument("--insecure", action="store_true", help="do not verify HTTPS certificates")
    return parser.parse_args(argv)


def main(argv=None):
    args = parse_args(argv)
    env = os.environ
    context = tls_context(args)
    source = Cluster("source", args.source, env.get("MMS_SOURCE_USER"), env.get("MMS_SOURCE_PASSWORD"),
                     context, args.timeout)
    target = Cluster("target", args.target, env.get("MMS_TARGET_USER"), env.get("MMS_TARGET_PASSWORD"),
                     context, args.timeout)
    try:
        source_name, source_version, source_uuid = source.info()
        target_name, target_version, target_uuid = target.info()
        print(f"source: {source_name} {source_version} at {source.url}")
        print(f"target: {target_name} {target_version} at {target.url}")
        if source_uuid and source_uuid == target_uuid:
            raise MigrationError("source and target are the same cluster")
        indices, skipped = select_indices(source, args.project)
        if skipped:
            print(f"not MMS indices, left alone: {', '.join(skipped)}")
        if not indices:
            raise MigrationError("no MMS indices (*_node, *_commit, *_metadata) found on the source")
        if args.dry_run:
            for index in indices:
                state = "exists on target" if target.exists(index) else "to create"
                print(f"  {index}: {source.count(index)} document(s), {state}")
            return 0
        if not args.verify_only:
            print(f"migrating {len(indices)} index(es) with --copy {args.copy}")
            for index in indices:
                migrate(source, target, index, args)
        print("verifying")
        results = [verify(source, target, index, args) for index in indices]
        failed = results.count(False)
        if failed:
            print(f"FAILED: {failed} of {len(indices)} index(es) do not match")
            return 1
        print(f"OK: {len(indices)} index(es) match")
        return 0
    except MigrationError as error:
        print(f"error: {error}", file=sys.stderr)
        return 2


if __name__ == "__main__":
    sys.exit(main())

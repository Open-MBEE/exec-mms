.. _search-migration:

=================================================================
Migrating Elasticsearch 7 data to Elasticsearch 9 or OpenSearch 3
=================================================================

MMS releases up to 4.0.20 ran against Elasticsearch 7. Elasticsearch 9 and OpenSearch 3 cannot open
indices that were created by Elasticsearch 7, so an existing deployment has to copy its indices into
a new cluster. ``elastic/scripts/migrate_search_indices.py`` does that copy. The procedure is the same
for both targets.

For every MMS index on the source (``<project>_node``, ``<project>_commit``, ``<project>_metadata``)
the script

#. creates the index on the target with the mapping file MMS ships for it (the node mapping is chosen
   by the project's ``schema``, as MMS does on project creation), adds the fields the source index
   mapped dynamically, and keeps the source's shard count, replica count, result window and
   mapping limits;
#. copies every document with its id;
#. compares the document counts of source and target.

The source cluster is only read, and the relational database and the S3 store are not touched.

Prerequisites
-------------

- Python 3.6 or later on the machine that runs the script. No packages are needed.
- A checkout of the MMS version you are upgrading to. The script reads the mappings from
  ``elastic/src/main/resources/elastic_mappings``; use ``--mappings`` if yours are elsewhere.
- The Elasticsearch 7 cluster, still running, and a new, empty Elasticsearch 9 or OpenSearch 3
  cluster. The machine that runs the script must reach both over HTTP(S).
- A user that can read the indices on the source and create and write indices on the target, if the
  clusters require authentication.
- For the default copy mode (``--copy remote``) the target pulls the documents itself. The target
  must reach the source, and every target node must allow it in its configuration before it starts:

  - Elasticsearch: ``reindex.remote.whitelist: "old-es-host:9200"``
  - OpenSearch: ``reindex.remote.allowlist: "old-es-host:9200"``

  If you cannot change the target's configuration, or the target cannot reach the source, use
  ``--copy client``: the script then reads the source and bulk-indexes into the target, and nothing
  has to be configured on either cluster.

How to run
----------

#. Stop MMS, so nothing is written during the copy. Leave the database and the S3 store as they are.
#. See what would be migrated::

     python3 elastic/scripts/migrate_search_indices.py \
         --source http://old-es:9200 --target http://new-search:9200 --dry-run

#. Migrate::

     python3 elastic/scripts/migrate_search_indices.py \
         --source http://old-es:9200 --target http://new-search:9200

   The script exits with 0 when every index was copied and its count matches, 1 when a count does not
   match, and 2 when it stopped on an error.
#. Point MMS at the new cluster with ``elasticsearch.host``, ``elasticsearch.port`` and
   ``elasticsearch.http`` (and ``elasticsearch.username`` / ``elasticsearch.password``), keep the
   same database and S3 settings, and start the new MMS version.

Useful options:

``--copy client``
    Copy through the script instead of reindex-from-remote.

``--remote-host URL``
    The source URL as the target reaches it, when that differs from ``--source`` (for example a
    container or internal host name). Only used by ``--copy remote``.

``--project ID``
    Migrate only this project. Repeat it for several projects.

``--existing fail|skip|overwrite``
    What to do when an index already exists on the target. The default stops. ``skip`` leaves it
    alone, which resumes an interrupted run; ``overwrite`` deletes and copies it again.

``--batch-size N``
    Documents per page (default 1000). Lower it if documents are very large.

Credentials are read from the environment so they do not appear in the process list:
``MMS_SOURCE_USER`` / ``MMS_SOURCE_PASSWORD`` and ``MMS_TARGET_USER`` / ``MMS_TARGET_PASSWORD``.
With ``--copy remote`` the source credentials are passed to the target in the reindex request.
For HTTPS, ``--ca-file`` names the CA bundle for the script's own connections and ``--insecure``
turns certificate checks off. With ``--copy remote`` the target's connection to an HTTPS source is
configured on the target (``reindex.ssl.*``), not by the script.

How to verify
-------------

The migration already ends with a count comparison. To check again later, or more thoroughly::

    python3 elastic/scripts/migrate_search_indices.py \
        --source http://old-es:9200 --target http://new-search:9200 --verify-only --deep-verify

``--deep-verify`` reads both clusters completely and compares the id and content of every document,
so it takes about as long as the copy. Run the verification before MMS writes to the new cluster;
afterwards the counts differ legitimately.

Then check MMS itself, for example for one project::

    curl -u user:password http://mms:8080/projects/<project>/refs/master/commits
    curl -u user:password http://mms:8080/projects/<project>/refs/master/elements
    curl -u user:password "http://mms:8080/projects/<project>/refs/master/search?id=<element id>"

How to roll back
----------------

The script never changes the source cluster. Until MMS has written to the new cluster, rolling back
is: stop the new MMS, start the old MMS version with its old ``elasticsearch.*`` settings.

To redo a migration, delete the affected indices on the target, or run again with
``--existing overwrite``.

Once the new MMS has accepted writes, the old cluster no longer matches the database. Going back
then means restoring the database and S3 backups taken before the upgrade together with the old
cluster, so take those backups before starting MMS on the new cluster, and keep the old cluster until
the new deployment is accepted.

Limits
------

- Only indices named ``*_node``, ``*_commit`` and ``*_metadata`` are migrated. Other indices are
  listed and left alone, as are aliases, index templates and custom analysis settings.
- An index is copied in one pass without parallel slices; plan the downtime for large projects by
  timing one project first with ``--project``.
- ``--copy client`` parses and re-writes each document, so a number can be written differently
  (``1.0E3`` becomes ``1000.0``) while keeping the same value. ``--copy remote`` does not do this.
- A document that the shipped mapping rejects stops the run with the server's error; nothing is
  skipped silently.

package org.openmbee.mms.elastic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.openmbee.mms.core.exceptions.InternalErrorException;
import org.openmbee.mms.elastic.utils.BulkProcessor;
import org.openmbee.mms.elastic.utils.ElasticClient;
import org.openmbee.mms.elastic.utils.Index;
import org.openmbee.mms.json.BaseJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

public abstract class BaseElasticDAOImpl<E extends Map<String, Object>> {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${elasticsearch.limit.result:10000}")
    protected int resultLimit;

    @Value("${elasticsearch.limit.term:1000}")
    protected int termLimit;

    @Value("${elasticsearch.limit.get:100000}")
    protected int getLimit;

    @Value("${elasticsearch.limit.index:5000}")
    protected int bulkLimit;

    protected ElasticClient client;

    @Autowired
    public void setElasticClient(@Qualifier("clientElastic") ElasticClient client) {
        this.client = client;
    }

    protected String getIndex() {
        return Index.BASE.get();
    }

    protected abstract E newInstance();

    public void deleteById(String index, String docId) {
        try {
            client.requestAllowingNotFound("DELETE", docEndpoint(index, docId), Collections.emptyMap(), null);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new InternalErrorException(e);
        }
    }

    public void deleteAll(String index, Collection<? extends BaseJson> jsons) {
        BulkProcessor bulkProcessor = new BulkProcessor(client, Integer.MAX_VALUE);
        for (BaseJson json : jsons) {
            bulkProcessor.delete(index, json.getDocId());
        }
        bulkProcessor.closeIgnoringFailures();
    }

    public boolean existsById(String index, String docId) {
        try {
            return client.exists(docEndpoint(index, docId), Map.of("_source", "false", "stored_fields", "_none_"));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new InternalErrorException(e);
        }
    }

    public Optional<E> findById(String index, String docId) {
        try {
            Map<String, Object> res = client
                .requestAllowingNotFound("GET", docEndpoint(index, docId), Collections.emptyMap(), null);
            if (res.containsKey("error")) {
                throw new IOException(String.valueOf(res.get("error")));
            }
            return toInstance(res);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new InternalErrorException(e);
        }
    }

    public List<E> findAllById(String index, Set<String> docIds) {
        try {
            List<E> listOfResponses = new ArrayList<>();

            if (docIds.isEmpty()) {
                return listOfResponses;
            }
            List<Map<String, String>> docs = new ArrayList<>();
            for (String eid : docIds) {
                docs.add(Map.of("_index", index, "_id", eid));
                if (docs.size() == getLimit) {
                    getResponses(docs, listOfResponses);
                    docs = new ArrayList<>();
                }
            }
            if (!docs.isEmpty()) {
                getResponses(docs, listOfResponses);
            }
            return listOfResponses;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new InternalErrorException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void getResponses(List<Map<String, String>> docs, List<E> listOfResponses) throws IOException {
        Map<String, Object> response = client
            .request("POST", "/_mget", Collections.emptyMap(), Map.of("docs", docs));

        for (Map<String, Object> item : (List<Map<String, Object>>) response.get("docs")) {
            toInstance(item).ifPresent(listOfResponses::add);
        }
    }

    public void indexAll(String index, Collection<? extends BaseJson> jsons) {
        BulkProcessor bulkProcessor = getBulkProcessor(client);
        for (BaseJson json : jsons) {
            bulkProcessor.index(index, json.getDocId(), json);
        }
        bulkProcessor.close();
    }

    public void index(String index, BaseJson<?> json) {
        try {
            if (json.getDocId() == null) {
                client.request("POST", ElasticClient.endpoint(index, "_doc"), Collections.emptyMap(), json);
            } else {
                client.request("PUT", docEndpoint(index, json.getDocId()), Collections.emptyMap(), json);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new InternalErrorException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public E update(String index, BaseJson json) {
        E response = newInstance();
        response.putAll(json);
        try {
            Map<String, Object> request = new LinkedHashMap<>();
            request.put("doc", json);
            request.put("doc_as_upsert", true);
            request.put("upsert", json);
            Map<String, Object> updateResponse = client.request("POST",
                ElasticClient.endpoint(index, "_update", json.getDocId()), Map.of("_source", "true"), request);
            Object result = updateResponse.get("result");
            if ("created".equals(result) || "updated".equals(result)) {
                Object get = updateResponse.get("get");
                if (get instanceof Map) {
                    toInstance((Map<String, Object>) get).ifPresent(response::putAll);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new InternalErrorException(e);
        }
        return response;
    }

    protected Map<String, Object> search(String index, Map<String, Object> body) throws IOException {
        return client.request("POST", ElasticClient.endpoint(index, "_search"), Collections.emptyMap(), body);
    }

    protected BulkProcessor getBulkProcessor(ElasticClient client) {
        return new BulkProcessor(client, bulkLimit);
    }

    private static String docEndpoint(String index, String docId) {
        return ElasticClient.endpoint(index, "_doc", docId);
    }

    @SuppressWarnings("unchecked")
    private Optional<E> toInstance(Map<String, Object> doc) {
        if (!Boolean.TRUE.equals(doc.get("found"))) {
            return Optional.empty();
        }
        E ob = newInstance();
        Object source = doc.get("_source");
        if (source instanceof Map) {
            ob.putAll((Map<String, Object>) source);
        }
        return Optional.of(ob);
    }
}

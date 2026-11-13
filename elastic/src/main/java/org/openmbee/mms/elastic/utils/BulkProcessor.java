package org.openmbee.mms.elastic.utils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openmbee.mms.core.exceptions.InternalErrorException;

public class BulkProcessor {

    protected int bulkLimit;
    ElasticClient client;
    StringBuilder actions = new StringBuilder();
    int numberOfActions = 0;

    public BulkProcessor(ElasticClient client, int bulkLimit) {
        this.client = client;
        this.bulkLimit = bulkLimit;
    }

    public void index(String index, String docId, Object source) {
        add("index", index, docId, source);
    }

    public void update(String index, String docId, Object update) {
        add("update", index, docId, update);
    }

    public void delete(String index, String docId) {
        add("delete", index, docId, null);
    }

    private void add(String operation, String index, String docId, Object body) {
        Map<String, Object> target = new LinkedHashMap<>();
        target.put("_index", index);
        if (docId != null) {
            target.put("_id", docId);
        }
        try {
            actions.append(client.toJson(Map.of(operation, target))).append('\n');
            if (body != null) {
                actions.append(client.toJson(body)).append('\n');
            }
        } catch (IOException ioe) {
            throw new InternalErrorException(ioe);
        }
        numberOfActions++;
        clear();
    }

    public void clear() {
        if (numberOfActions >= bulkLimit) {
            bulkBatchRequests();
        }
    }

    public void close() {
        if (numberOfActions > 0) {
            bulkBatchRequests();
        }
    }

    /**
     * Sends what has been added without checking the outcome of the individual actions.
     */
    public void closeIgnoringFailures() {
        if (numberOfActions > 0) {
            send();
        }
    }

    protected void bulkBatchRequests() {
        Map<String, Object> response = send();
        if (Boolean.TRUE.equals(response.get("errors"))) {
            throw new InternalErrorException(buildFailureMessage(response));
        }
    }

    private Map<String, Object> send() {
        try {
            Map<String, Object> response = client.bulk(actions.toString());
            actions = new StringBuilder();
            numberOfActions = 0;
            return response;
        } catch (IOException ioe) {
            throw new InternalErrorException(ioe);
        }
    }

    @SuppressWarnings("unchecked")
    private static String buildFailureMessage(Map<String, Object> response) {
        StringBuilder message = new StringBuilder("failure in bulk execution:");
        List<Map<String, Map<String, Object>>> items = (List<Map<String, Map<String, Object>>>) response.get("items");
        for (int i = 0; i < items.size(); i++) {
            for (Map<String, Object> item : items.get(i).values()) {
                Object error = item.get("error");
                if (error != null) {
                    message.append("\n[").append(i).append("]: index [").append(item.get("_index"))
                        .append("], id [").append(item.get("_id")).append("], message [").append(error).append("]");
                }
            }
        }
        return message.toString();
    }
}

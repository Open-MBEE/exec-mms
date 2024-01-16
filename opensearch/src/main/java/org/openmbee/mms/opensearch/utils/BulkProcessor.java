package org.openmbee.mms.opensearch.utils;

import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.update.UpdateRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.openmbee.mms.core.exceptions.InternalErrorException;

import java.io.IOException;

public class BulkProcessor {

    protected int bulkLimit;
    RestHighLevelClient client;
    RequestOptions options;
    BulkRequest bulkRequest = new BulkRequest();

    public BulkProcessor(RestHighLevelClient client, RequestOptions options, int bulkLimit) {
        this.client = client;
        this.options = options;
        this.bulkLimit = bulkLimit;
    }

    public void add(IndexRequest action) {
        bulkRequest.add(action);
        clear();
    }

    public void add(UpdateRequest action) {
        bulkRequest.add(action);
        clear();
    }

    public void clear() {
        if (bulkRequest.numberOfActions() >= bulkLimit) {
            bulkBatchRequests();
        }
    }

    public void close() {
        if (bulkRequest.numberOfActions() > 0) {
            bulkBatchRequests();
        }
    }

    protected void bulkBatchRequests() {
        try {
            BulkResponse response = client.bulk(bulkRequest, options);
            if (response.hasFailures()) {
                String failure = response.buildFailureMessage();
                throw new InternalErrorException(failure);
            }
            bulkRequest = new BulkRequest();
        } catch (IOException ioe) {
            throw new InternalErrorException(ioe);
        }
    }
}

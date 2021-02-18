package org.openmbee.mms.elastic.utils;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
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

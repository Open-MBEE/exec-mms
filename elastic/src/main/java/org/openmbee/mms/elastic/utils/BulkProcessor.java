package org.openmbee.mms.elastic.utils;

import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.openmbee.mms.core.exceptions.InternalErrorException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BulkProcessor {

    protected int bulkLimit;
    RestHighLevelClient client;
    RequestOptions options;
    List<DocWriteRequest<?>> allRequests = new ArrayList<>();

    public BulkProcessor(RestHighLevelClient client, RequestOptions options, int bulkLimit) {
        this.client = client;
        this.options = options;
        this.bulkLimit = bulkLimit;
    }

    public void add(IndexRequest action) {
        allRequests.add(action);
        clear();
    }

    public void add(UpdateRequest action) {
        allRequests.add(action);
        clear();
    }

    public void clear() {
        if (allRequests.size() >= bulkLimit) {
            bulkBatchRequests(allRequests);
            allRequests = new ArrayList<>();
        }
    }

    public void close() {
        bulkBatchRequests(allRequests);
    }

    protected void bulkBatchRequests(List<DocWriteRequest<?>> actionRequest) {
        BulkRequest bulkRequest = new BulkRequest();
        actionRequest.forEach(bulkRequest::add);
        try {
            client.bulk(bulkRequest, options);
        } catch (IOException ioe) {
            throw new InternalErrorException(ioe);
        }
    }
}

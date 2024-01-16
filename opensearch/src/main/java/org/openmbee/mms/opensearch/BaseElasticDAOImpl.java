package org.openmbee.mms.opensearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.opensearch.action.DocWriteResponse;
import org.opensearch.action.bulk.BulkRequest;

import org.opensearch.action.delete.DeleteRequest;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.get.GetResponse;
import org.opensearch.action.get.MultiGetItemResponse;
import org.opensearch.action.get.MultiGetRequest;
import org.opensearch.action.get.MultiGetResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.update.UpdateRequest;
import org.opensearch.action.update.UpdateResponse;
import org.opensearch.client.HttpAsyncResponseConsumerFactory;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.get.GetResult;
import org.opensearch.search.fetch.subphase.FetchSourceContext;
import org.openmbee.mms.core.exceptions.InternalErrorException;
import org.openmbee.mms.opensearch.utils.BulkProcessor;
import org.openmbee.mms.opensearch.utils.Index;
import org.openmbee.mms.json.BaseJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

public abstract class BaseElasticDAOImpl<E extends Map<String, Object>> {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${opensearch.limit.result:10000}")
    protected int resultLimit;

    @Value("${opensearch.limit.term:1000}")
    protected int termLimit;

    @Value("${opensearch.limit.get:100000}")
    protected int getLimit;

    @Value("${opensearch.limit.index:5000}")
    protected int bulkLimit;

    protected static int readTimeout = 1000000000;
    protected RestHighLevelClient client;
    protected static final RequestOptions REQUEST_OPTIONS;
    static {
        RequestOptions.Builder requestBuilder = RequestOptions.DEFAULT.toBuilder();
        // TODO: Should be configureable
        requestBuilder.setHttpAsyncResponseConsumerFactory(new HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory(1024 * 1024 * 1024));
        REQUEST_OPTIONS = requestBuilder.build();
    }

    @Autowired
    public void setRestHighLevelClient(@Qualifier("clientElastic") RestHighLevelClient client) {
        this.client = client;
    }

    protected String getIndex() {
        return Index.BASE.get();
    }

    protected abstract E newInstance();

    public void deleteById(String index, String docId) {
        try {
            client.delete(new DeleteRequest(index, docId), REQUEST_OPTIONS);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new InternalErrorException(e);
        }
    }

    public void deleteAll(String index, Collection<? extends BaseJson> jsons) {
        try {
            BulkRequest bulkIndex = new BulkRequest();
            for (BaseJson json : jsons) {
                bulkIndex.add(new DeleteRequest(index, json.getDocId()));
            }
            client.bulk(bulkIndex, REQUEST_OPTIONS);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new InternalErrorException(e);
        }
    }

    public boolean existsById(String index, String docId) {
        try {
            GetRequest getRequest = new GetRequest(index, docId);
            getRequest.fetchSourceContext(new FetchSourceContext(false));
            getRequest.storedFields("_none_");
            return client.exists(getRequest, REQUEST_OPTIONS);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new InternalErrorException(e);
        }
    }

    public Optional<E> findById(String index, String docId) {
        try {
            GetResponse res = client
                .get(new GetRequest(index, docId), REQUEST_OPTIONS);
            if (res.isExists()) {
                E ob = newInstance();
                ob.putAll(res.getSourceAsMap());
                return Optional.of(ob);
            } else {
                return Optional.empty();
            }
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
            int cur = 0;
            MultiGetRequest request = new MultiGetRequest();
            for (String eid : docIds) {
                request.add(index, eid);
                cur++;
                if (cur == getLimit) {
                    getResponses(request, listOfResponses);
                    cur = 0;
                    request = new MultiGetRequest();
                }
            }
            if (cur > 0) {
                getResponses(request, listOfResponses);
            }
            return listOfResponses;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new InternalErrorException(e);
        }
    }

    private void getResponses(MultiGetRequest request, List<E> listOfResponses) throws IOException {
        MultiGetResponse response = client.mget(request, REQUEST_OPTIONS);

        for (MultiGetItemResponse res : response.getResponses()) {
            GetResponse item = res.getResponse();
            if (item != null && item.isExists()) {
                E ob = newInstance();
                ob.putAll(item.getSourceAsMap());
                listOfResponses.add(ob);
            } else {
                continue;
            }
        }
    }

    public void indexAll(String index, Collection<? extends BaseJson> jsons) {
        BulkProcessor bulkProcessor = getBulkProcessor(client);
        for (BaseJson json : jsons) {
            bulkProcessor.add(new IndexRequest(index).id(json.getDocId()).source(json));
        }
        bulkProcessor.close();
    }

    public void index(String index, BaseJson<?> json) {
        try {
            client.index(new IndexRequest(index).id(json.getDocId()).source(json), REQUEST_OPTIONS);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new InternalErrorException(e);
        }
    }

    public E update(String index, BaseJson json) {
        E response = newInstance();
        response.putAll(json);
        try {
            UpdateRequest request = new UpdateRequest(index, json.getDocId());
            request.fetchSource(true);
            request.docAsUpsert(true).doc(json).upsert(json);
            UpdateResponse updateResponse = client.update(request, REQUEST_OPTIONS);
            if (updateResponse.getResult() == DocWriteResponse.Result.CREATED ||
                updateResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                GetResult result = updateResponse.getGetResult();
                if (result.isExists()) {
                    response.putAll(result.sourceAsMap());
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new InternalErrorException(e);
        }
        return response;
    }

    protected BulkProcessor getBulkProcessor(RestHighLevelClient client) {
        return new BulkProcessor(client, REQUEST_OPTIONS, bulkLimit);
    }
}

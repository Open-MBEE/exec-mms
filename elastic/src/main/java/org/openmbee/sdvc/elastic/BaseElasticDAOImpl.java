package org.openmbee.sdvc.elastic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.openmbee.sdvc.core.exceptions.SdvcException;
import org.openmbee.sdvc.elastic.utils.Index;
import org.openmbee.sdvc.json.BaseJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

public abstract class BaseElasticDAOImpl<E extends Map<String, Object>> {

    private final Logger logger = LogManager.getLogger(getClass());

    @Value("${elasticsearch.limit.result}")
    protected int resultLimit;
    @Value("${elasticsearch.limit.term}")
    protected int termLimit;
    protected static int readTimeout = 1000000000;
    protected RestHighLevelClient client;
    private static final RequestOptions REQUEST_OPTIONS;
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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }

    public boolean existsById(String index, String docId) {
        try {
            GetRequest getRequest = new GetRequest(index, docId);
            getRequest.fetchSourceContext(new FetchSourceContext(false));
            getRequest.storedFields("_none_");
            return client.exists(getRequest, REQUEST_OPTIONS);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }

    public List<E> findAllById(String index, Set<String> docIds) {
        try {
            List<E> listOfResponses = new ArrayList<>();

            if (docIds.isEmpty()) {
                return listOfResponses;
            }
            MultiGetRequest request = new MultiGetRequest();
            for (String eid : docIds) {
                request.add(index, eid);
            }
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
            return listOfResponses;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void indexAll(String index, Collection<? extends BaseJson> jsons) {
        BulkProcessor bulkProcessor = getBulkProcessor(client);
        try {
            if(!bulkProcessor.awaitClose(1200L, TimeUnit.SECONDS)) {
                logger.error("Timed out in bulk processing");
            }
        } catch (InterruptedException e) {
            logger.error(e);
        }

    }

    public void index(String index, BaseJson<?> json) {
        try {
            client.index(new IndexRequest(index).id(json.getDocId()).source(json),
                REQUEST_OPTIONS);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            // TODO: Handle other getResults maybe
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    private static BulkProcessor getBulkProcessor(RestHighLevelClient client) {
        return getBulkProcessor(client, null);
    }

    private static BulkProcessor getBulkProcessor(RestHighLevelClient client,  BulkProcessor.Listener listener) {
        if (listener == null) {
            listener = new BulkProcessor.Listener() {
                @Override
                public void beforeBulk(long executionId, BulkRequest request) {
                }

                @Override
                public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                }

                @Override
                public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                }
            };
        }
        BulkProcessor.Builder bpBuilder = BulkProcessor.builder((request, bulkListener) -> client
            .bulkAsync(request, RequestOptions.DEFAULT, bulkListener), listener);
        bpBuilder.setBulkActions(5000);
        bpBuilder.setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB));
        bpBuilder.setConcurrentRequests(1);
        bpBuilder.setFlushInterval(TimeValue.timeValueSeconds(5));
        bpBuilder.setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueMillis(100), 3));

        return bpBuilder.build();
    }
}

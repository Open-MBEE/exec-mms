package org.openmbee.sdvc.elastic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.openmbee.sdvc.elastic.utils.Index;
import org.openmbee.sdvc.json.BaseJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

public abstract class BaseElasticDAOImpl<E extends Map<String, Object>> {

    @Value("${elasticsearch.limit.result}")
    protected int resultLimit;
    @Value("${elasticsearch.limit.term}")
    protected int termLimit;
    protected static int readTimeout = 1000000000;
    protected RestHighLevelClient client;

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
            client.delete(new DeleteRequest(index, docId), RequestOptions.DEFAULT);
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
            client.bulk(bulkIndex, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsById(String index, String docId) {
        try {
            GetRequest getRequest = new GetRequest(index, docId);
            getRequest.fetchSourceContext(new FetchSourceContext(false));
            getRequest.storedFields("_none_");
            return client.exists(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<E> findById(String index, String docId) {
        try {
            GetResponse res = client
                .get(new GetRequest(index, docId), RequestOptions.DEFAULT);
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
            MultiGetResponse response = client.mget(request, RequestOptions.DEFAULT);

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
        try {
            BulkRequest bulkIndex = new BulkRequest();
            for (BaseJson json : jsons) {
                bulkIndex.add(new IndexRequest(index).id(json.getDocId()).source(json));
            }
            client.bulk(bulkIndex, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void index(String index, BaseJson json) {
        try {
            client.index(new IndexRequest(index).id(json.getDocId()).source(json),
                RequestOptions.DEFAULT);
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
            UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);
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
}

package org.openmbee.sdvc.elastic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.openmbee.sdvc.core.config.DbContextHolder;
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
    protected final String type = "_doc";
    protected RestHighLevelClient client;

    @Autowired
    public void setRestHighLevelClient(@Qualifier("clientElastic") RestHighLevelClient client) {
        this.client = client;
    }

    protected String getIndex() {
        return DbContextHolder.getContext().getProjectId().toLowerCase();
    }

    protected abstract E newInstance();

    public void deleteById(String index, String indexId) {
        try {
            client.delete(new DeleteRequest(index, this.type, indexId), RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAll(String index, Collection<? extends BaseJson> jsons) {
        try {
            BulkRequest bulkIndex = new BulkRequest();
            for (BaseJson json : jsons) {
                bulkIndex.add(new DeleteRequest(index, this.type, json.getIndexId()));
            }
            client.bulk(bulkIndex, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsById(String index, String indexId) {
        try {
            GetRequest getRequest = new GetRequest(index, this.type, indexId);
            getRequest.fetchSourceContext(new FetchSourceContext(false));
            getRequest.storedFields("_none_");
            return client.exists(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<E> findById(String index, String indexId) {
        try {
            GetResponse res = client
                .get(new GetRequest(index, type, indexId), RequestOptions.DEFAULT);
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

    public List<E> findAllById(String index, Set<String> indexIds) {
        try {
            List<E> listOfResponses = new ArrayList<>();

            if (indexIds.isEmpty()) {
                return listOfResponses;
            }
            MultiGetRequest request = new MultiGetRequest();
            for (String eid : indexIds) {
                request.add(index, this.type, eid);
            }
            MultiGetResponse response = client.mget(request, RequestOptions.DEFAULT);

            for (MultiGetItemResponse res : response.getResponses()) {
                GetResponse item = res.getResponse();
                if (item.isExists()) {
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
                bulkIndex.add(new IndexRequest(index, this.type, json.getIndexId()).source(json));
            }
            client.bulk(bulkIndex, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void index(String index, BaseJson json) {
        try {
            client.index(new IndexRequest(index, this.type, json.getIndexId()).source(json),
                RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

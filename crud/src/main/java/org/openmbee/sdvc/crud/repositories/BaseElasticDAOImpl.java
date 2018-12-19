package org.openmbee.sdvc.crud.repositories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.elasticsearch.ElasticsearchParseException;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.json.BaseJson;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

public abstract class BaseElasticDAOImpl {

    protected RestHighLevelClient client;
    @Value("${elastic.limit.result}")
    protected static int resultLimit;
    @Value("${elastic.limit.term}")
    protected static int termLimit;
    protected static int readTimeout = 1000000000;
    // :TODO save, saveAll --> updates have details of upsert method, should break out into helper method for create/update

    @Autowired
    public void setRestHighLevelClient(@Qualifier("clientElastic") RestHighLevelClient client) {
        this.client = client;
    }

    public String getIndex() {
        return DbContextHolder.getContext().getIndex();
    }

    public long count() {
        // Returns the number of entities available.
        return 0;
    }

    public void delete(BaseJson json) throws IOException {
        // :TODO deletes by entity
    }

    public void deleteById(String index, String indexId) throws IOException {
        client.delete(new DeleteRequest(index, null, indexId), RequestOptions.DEFAULT);
    }

    public void deleteAll(String index, Collection<? extends BaseJson> jsons) throws IOException {
        BulkRequest bulkIndex = new BulkRequest();
        for (BaseJson json : jsons) {
            bulkIndex.add(new DeleteRequest(index, null, json.getId()));
        }
        client.bulk(bulkIndex, RequestOptions.DEFAULT);
    }

    public boolean existsById(String index, String indexId) throws IOException {
        GetRequest getRequest = new GetRequest(index, null, indexId);
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        return client.exists(getRequest, RequestOptions.DEFAULT);
    }

    public List<Map<String, Object>> findAll() {
        //:TODO Returns all instances of the type.  Returns all entities. So not the elasticID
        return null;
    }

    public Map<String, Object> findById(String index, String indexId) throws IOException {
        return client.get(new GetRequest(index, null, indexId), RequestOptions.DEFAULT).getSourceAsMap();
    }

    public List<Map<String, Object>> findAllById(String index, Set<String> indexIds)
        throws IOException {
        List<Map<String, Object>> listOfResponses = new ArrayList<>();

        MultiGetRequest request = new MultiGetRequest();
        for (String eid : indexIds) {
            request.add(index, null, eid);
        }
        MultiGetResponse response = client.mget(request, RequestOptions.DEFAULT);

        for (MultiGetItemResponse res : response.getResponses()) {
            GetResponse item = res.getResponse();
            if (item.isExists()) {
                listOfResponses.add(item.getSourceAsMap());
            } else {
                //:TODO what do we want to do for missing ids?
                continue;
            }
        }
        return listOfResponses;
    }

    public List<Map<String, Object>> findByIndexIds(String index, Set<String> indexIds) {
        List<Map<String, Object>> maps = new ArrayList<>();
        int i = 97;
        for (String eid : indexIds) {
            BaseJson json = new BaseJson();
            json.setIndexId(eid);
            json.setModified("2018-12-08T01:25:00.117-0700");
            json.setId(Character.toString((char) i));
            json.setName(json.getId());
            maps.add(json);
            i++;
        }
        /*
        BaseJson baseJson = new BaseJson();
        baseJson.setId("testing");
        baseJson.setName("element1");
        baseJson.getIndexId("8a1ee2ef-078f-4f3f-ae89-66fb5e9e7bba");
        baseJson.setModified("2015-07-04T12:08:56.235-0700");

        List<Map<String, Object>> maps = new ArrayList<>();
        maps.add(baseJson);
        */
        return maps;
    }

    public Map<String, Object> findByIndexId(String indexId) {
        return null;
    }

    public void indexAll(String index, Collection<? extends BaseJson> jsons) throws IOException {
        BulkRequest bulkIndex = new BulkRequest();
        for (BaseJson json : jsons) {
            bulkIndex.add(new IndexRequest((index)).source(json));
        }
        client.bulk(bulkIndex, RequestOptions.DEFAULT);
    }

    public void index(String index, BaseJson json) throws IOException {
        client.index(new IndexRequest(index).source(json), RequestOptions.DEFAULT);
    }
}

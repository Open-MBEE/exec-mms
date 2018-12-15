package org.openmbee.sdvc.crud.repositories.commit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.openmbee.sdvc.crud.repositories.BaseElasticDAOImpl;
import org.openmbee.sdvc.json.BaseJson;
import org.springframework.stereotype.Component;

@Component
public class CommitElasticDAOImpl extends BaseElasticDAOImpl implements CommitIndexDAO {


    public List<Map<String, Object>> findByIndexIds(Set<String> indexIds) {
        return null;
    }

    public void indexAll(Collection<? extends BaseJson> jsons) throws IOException {
        this.indexAll("projectId_commit", jsons);
    }

    public void index(BaseJson json) throws IOException {
        this.index("projectId_commit", json);
    }
    public Map<String, Object> findById(String indexId) throws IOException  {
        return this.findById("projectId_commit", indexId);
    }

    public List<Map<String, Object>> findAllById(Set<String> indexIds) throws IOException {
        return this.findAllById("projectId_commit", indexIds);
    }
    
    public void deleteById(String indexId) throws IOException{
        this.deleteById("projectId_commit", indexId);
    }

    public void deleteAll(Collection<? extends BaseJson> jsons) throws IOException{
        this.deleteAll("projectId_commit", jsons);
    }

    public boolean existsById(String indexId)throws IOException{
        return this.existsById("projectId_commit", indexId);
    }

    /**
     * Gets the JSON document of a bool : should commit query
     * result printed as Json looks like:
     * {
     *   "bool":{"should":[{"term":{"added.id":sysmlid}},
     *                     {"term":{"updated.id":sysmlid}},
     *                     {"term":{"deleted.id":sysmlid}}]}
     * }
     * @param id the sysmlid to add to the term search
     * @return QueryBuilder q
     */
    private QueryBuilder getCommitHistoryQuery(String id) {
        QueryBuilder matchQueryBuilder = QueryBuilders.boolQuery();
        QueryBuilder addedQuery = QueryBuilders.termQuery("added.id", id);
        QueryBuilder updatedQuery = QueryBuilders.termQuery("updated.id", id);
        QueryBuilder deletedQuery = QueryBuilders.termQuery("deleted.id", id);
        QueryBuilder query = QueryBuilders.boolQuery().should(addedQuery).should(updatedQuery).should(deletedQuery);
        return query;
    }

    /**
     * Returns the commit history of a element                           (1)
     * <p> Returns a JSONArray of objects that look this:
     * {
     * "id": "commitId",
     * "_timestamp": "timestamp",
     * "_creator": "creator"
     * }                                                                (2)
     * <p>
     *
     * @param id sysmlId     (3)
     * @return JSONArray array or empty json array
     */
    @Override
    public List<Map<String,Object>> getCommitHistory(String index, String id) throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        QueryBuilder query = getCommitHistoryQuery(id);
        //new FieldSortBuilder("_uid").order(SortOrder.ASC)
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("user", "kimchy"));
        sourceBuilder.size(this.resultLimit);
        sourceBuilder.sort(new FieldSortBuilder("created").order(SortOrder.DESC));
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //:TODO get inner object of source
//            Map<String, Object> innerObject =
//                (Map<String, Object>) hit.sourceAsMap("innerObject");
        }
//
//        if (result.isSucceeded() && result.getTotal() > 0) {
//            JsonArray hits = result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
//            for (int i = 0; i < hits.size(); i++) {
//                JsonObject o = new JsonObject();
//                JsonObject record = hits.get(i).getAsJsonObject().getAsJsonObject("_source");
//                o.add(Sjm.SYSMLID, hits.get(i).getAsJsonObject().get("_id"));
//                o.add(Sjm.CREATED, record.get(Sjm.CREATED));
//                o.add(Sjm.CREATOR, record.get(Sjm.CREATOR));
//                if (record.has(Sjm.COMMENT)) {
//                    o.add(Sjm.COMMENT, record.get(Sjm.COMMENT));
//                }
//                array.add(o);
//            }
//        } else if (!result.isSucceeded()) {
//            throw new IOException(String.format("Elasticsearch error[%1$s]:%2$s",
//                result.getResponseCode(), result.getErrorMessage()));
//        }
//        return array;
        return new ArrayList<Map<String, Object>>();
    }

}

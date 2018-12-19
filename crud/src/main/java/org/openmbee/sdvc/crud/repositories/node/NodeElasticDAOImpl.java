package org.openmbee.sdvc.crud.repositories.node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.repositories.BaseElasticDAOImpl;
import org.openmbee.sdvc.json.BaseJson;
import org.springframework.stereotype.Component;

@Component
public class NodeElasticDAOImpl extends BaseElasticDAOImpl implements NodeIndexDAO {

    public Map<String, Object> findByIndexId(String indexId) {
        return null;
    }

    public List<Map<String, Object>> findByIndexIds(Set<String> indexIds) {
        return null;
    }

    public void indexAll(Collection<? extends BaseJson> jsons) throws IOException {
        this.indexAll(DbContextHolder.getContext().getProjectId() + "_node", jsons);

    }

    public void index(BaseJson json) throws IOException {
        this.index(DbContextHolder.getContext().getProjectId() + "_node", json);
    }

    public Map<String, Object> findById(String indexId) throws IOException {
        return this.findById(DbContextHolder.getContext().getProjectId() + "_node", indexId);
    }

    public List<Map<String, Object>> findAllById(Set<String> indexIds) throws IOException {
        return this.findAllById(DbContextHolder.getContext().getProjectId() + "_node", indexIds);
    }

    public void deleteById(String indexId) throws IOException {
        this.deleteById(DbContextHolder.getContext().getProjectId() + "_node", indexId);
    }

    public void deleteAll(Collection<? extends BaseJson> jsons) throws IOException {
        this.deleteAll(DbContextHolder.getContext().getProjectId() + "_node", jsons);
    }

    public boolean existsById(String indexId) throws IOException {
        return this.existsById("projectId_node", indexId);
    }

    @Override
    public Map<String, Object> getByCommitId(String commitIndexId, String nodeId, String index)
        throws IOException {
        Map<String, Object> commits;
        SearchRequest searchRequest = new SearchRequest();
        // searches the elements for the reference with the current commitId (elasticId) and sysmlid Id
        QueryBuilder query = QueryBuilders.boolQuery()
            .must(QueryBuilders.termQuery("commitId", commitIndexId))
            .must(QueryBuilders.termQuery("id", nodeId));
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(query);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        commits = searchResponse.getHits().getAt(0).getSourceAsMap();
        return commits;
    }

    @Override
    public List<Map<String, Object>> getElementsLessThanOrEqualTimestamp(String nodeId,
        String timestamp, List<String> refsCommitIds,
        String index) throws IOException {
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // build query :TODO build range query https://artifacts.elastic.co/javadoc/org/elasticsearch/elasticsearch/6.5.3/org/elasticsearch/index/query/RangeQueryBuilder.html
        QueryBuilders.rangeQuery("_modified").lte(timestamp);
        QueryBuilders.termQuery("commitId", refsCommitIds);



        searchSourceBuilder.query(QueryBuilders.termsQuery("commitId", refsCommitIds));
        searchSourceBuilder.size(2147483647);
        searchRequest.source(searchSourceBuilder);
        searchRequest.scroll(scroll);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        String scrollId = searchResponse.getScrollId();
        SearchHit[] searchHits = searchResponse.getHits().getHits();

        while (searchHits != null && searchHits.length > 0) {
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scroll(scroll);
            searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
            scrollId = searchResponse.getScrollId();
            searchHits = searchResponse.getHits().getHits();

        }

        // Clear the scroll value
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = client
            .clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        boolean succeeded = clearScrollResponse.isSucceeded();
        return null;
    }
    // Create filter array
//    int count = 0;
//        while (count < refsCommitIds.size()) {
//        List<String> sub = refsCommitIds.subList(count, Math.min(refsCommitIds.size(), count + termLimit));
//
//        JsonArray filter = new JsonArray();
//        JsonObject filt1 = new JsonObject();
//        JsonObject filtv = new JsonObject();
//        JsonObject filtv1 = new JsonObject();
//        filter.add(filt1);
//        filt1.add("range", filtv);
//        filtv.add("_modified", filtv1);
//        filtv1.addProperty("lte", timestamp);
//        JsonObject filt2 = new JsonObject();
//        JsonObject filt2v = new JsonObject();
//        filter.add(filt2);
//        filt2.add("terms", filt2v);
//        JsonUtil.addStringList(filt2v, Sjm.COMMITID, sub);
//        JsonObject filt3 = new JsonObject();
//        JsonObject filt3v = new JsonObject();
//        filter.add(filt3);
//        filt3.add("term", filt3v);
//        filt3v.addProperty(Sjm.SYSMLID, sysmlId);
//
//        // Create sort
//        JsonArray sort = new JsonArray();
//        JsonObject modified = new JsonObject();
//        JsonObject modifiedSortOpt = new JsonObject();
//        sort.add(modified);
//        modified.add("_modified", modifiedSortOpt);
//        modifiedSortOpt.addProperty("order", "desc");
//
//        // Add filter to bool, then bool to query
//        JsonObject query = new JsonObject();
//        JsonObject queryv = new JsonObject();
//        JsonObject bool = new JsonObject();
//        query.add("sort", sort);
//        query.add("query", queryv);
//        queryv.add("bool", bool);
//        bool.add("filter", filter);
//        // Add size limit
//        query.addProperty("size", "1");
//
//        Search search =
//            new Search.Builder(query.toString()).addIndex(index.toLowerCase().replaceAll("\\s+", "")).build();
//        SearchResult result;
//        try {
//            result = client.execute(search);
//
//            if (result.getTotal() > 0) {
//                JsonArray hits = result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
//                if (hits.size() > 0) {
//                    return hits.get(0).getAsJsonObject().getAsJsonObject("_source");
//                }
//            }
//        } catch (IOException e) {
//            logger.error(String.format("%s", LogUtil.getStackTrace(e)));
//        }
//        count += termLimit;
//    }
//        return null;
}


package org.openmbee.sdvc.elastic;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.openmbee.sdvc.crud.repositories.node.NodeIndexDAO;
import org.openmbee.sdvc.json.BaseJson;
import org.openmbee.sdvc.json.ElementJson;
import org.springframework.stereotype.Component;

@Component
public class NodeElasticDAOImpl extends BaseElasticDAOImpl<ElementJson> implements NodeIndexDAO {

    protected ElementJson newInstance() {
        return new ElementJson();
    }

    public void indexAll(Collection<? extends BaseJson> jsons) {
        this.indexAll(getIndex(), jsons);
    }

    public void index(BaseJson json) {
        this.index(getIndex(), json);
    }

    public Optional<ElementJson> findById(String indexId) {
        return this.findById(getIndex(), indexId);
    }

    public List<ElementJson> findAllById(Set<String> indexIds) {
        return this.findAllById(getIndex(), indexIds);
    }

    public void deleteById(String indexId) {
        this.deleteById(getIndex(), indexId);
    }

    public void deleteAll(Collection<? extends BaseJson> jsons) {
        this.deleteAll(getIndex(), jsons);
    }

    public boolean existsById(String indexId) {
        return this.existsById(getIndex(), indexId);
    }

    @Override
    public Optional<ElementJson> getByCommitId(String commitIndexId, String nodeId) {
        try {
            SearchRequest searchRequest = new SearchRequest(getIndex());
            // searches the elements for the reference with the current commitId (elasticId) and sysmlid Id
            QueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("_commitId", commitIndexId))
                .must(QueryBuilders.termQuery("id", nodeId));
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(query);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            if (searchResponse.getHits().getTotalHits() == 0) {
                return Optional.empty();
            }
            ElementJson ob = newInstance();
            ob.putAll(searchResponse.getHits().getAt(0).getSourceAsMap());
            return Optional.of(ob);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ElementJson> getElementLessThanOrEqualTimestamp(String nodeId,
        String timestamp, List<String> refsCommitIds) {
        try {
            final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
            SearchRequest searchRequest = new SearchRequest(getIndex());
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            // Query
            QueryBuilder query = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termsQuery("_commitId", refsCommitIds))
                .filter(QueryBuilders.termQuery("id", nodeId))
                .filter(QueryBuilders.rangeQuery("_modified").lte(timestamp));
            searchSourceBuilder.query(query);
            searchSourceBuilder.sort(new FieldSortBuilder("_modified").order(SortOrder.DESC));
            searchSourceBuilder.size(1);
            //searchSourceBuilder.size(this.resultLimit);
            searchRequest.source(searchSourceBuilder);
            //searchRequest.scroll(scroll);

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            //String scrollId = searchResponse.getScrollId();
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            // :TODO going to have to iterate through the inital results I think, have to test.
            // TODO This only returns one element, no need for scroll
            /*
            while (searchHits != null && searchHits.length > 0) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(scroll);
                searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
                scrollId = searchResponse.getScrollId();
                searchHits = searchResponse.getHits().getHits();
                for (SearchHit hit : searchHits) {
                    elements.add(hit.getSourceAsMap());
                }
            }

            // Clear the scroll value
            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollId);
            ClearScrollResponse clearScrollResponse = client
                .clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            boolean succeeded = clearScrollResponse.isSucceeded();*/
            return Optional.empty();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getIndex() {
        return super.getIndex() + "_node";
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
//                    return hits.read(0).getAsJsonObject().getAsJsonObject("_source");
//                }
//            }
//        } catch (IOException e) {
//            logger.error(String.format("%s", LogUtil.getStackTrace(e)));
//        }
//        count += termLimit;
//    }
//        return null;
}


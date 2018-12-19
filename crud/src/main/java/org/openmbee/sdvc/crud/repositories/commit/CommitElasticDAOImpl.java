package org.openmbee.sdvc.crud.repositories.commit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.repositories.BaseElasticDAOImpl;
import org.openmbee.sdvc.json.BaseJson;
import org.springframework.stereotype.Component;

@Component
public class CommitElasticDAOImpl extends BaseElasticDAOImpl implements CommitIndexDAO {


    public List<Map<String, Object>> findByIndexIds(Set<String> indexIds) {
        return null;
    }

    public void indexAll(Collection<? extends BaseJson> jsons) throws IOException {
        this.indexAll(DbContextHolder.getContext().getProjectId() + "_commit", jsons);
    }

    public void index(BaseJson json) throws IOException {
        this.index(DbContextHolder.getContext().getProjectId() + "_commit", json);
    }

    public Map<String, Object> findById(String indexId) throws IOException {
        Map<String, Object> response = this
            .findById(DbContextHolder.getContext().getProjectId() + "_commit", indexId)
            .orElse(new HashMap<>());
        return response;
    }

    public List<Map<String, Object>> findAllById(Set<String> indexIds) throws IOException {
        return this.findAllById(DbContextHolder.getContext().getProjectId() + "_commit", indexIds);
    }

    public void deleteById(String indexId) throws IOException {
        this.deleteById(DbContextHolder.getContext().getProjectId() + "_commit", indexId);
    }

    public void deleteAll(Collection<? extends BaseJson> jsons) throws IOException {
        this.deleteAll(DbContextHolder.getContext().getProjectId() + "_commit", jsons);
    }

    public boolean existsById(String indexId) throws IOException {
        return this.existsById(DbContextHolder.getContext().getProjectId() + "_commit", indexId);
    }

    /**
     * Gets the JSON document of a bool : should commit query result printed as Json looks like: {
     * "bool":{"should":[{"term":{"added.id":sysmlid}}, {"term":{"updated.id":sysmlid}},
     * {"term":{"deleted.id":sysmlid}}]} }
     *
     * @param id the sysmlid to add to the term search
     * @return QueryBuilder q
     */
    private QueryBuilder getCommitHistoryQuery(String id, Set<String> commitIds) {
        QueryBuilder addedQuery = QueryBuilders.termQuery("added.id", id);
        QueryBuilder updatedQuery = QueryBuilders.termQuery("updated.id", id);
        QueryBuilder deletedQuery = QueryBuilders.termQuery("deleted.id", id);
        QueryBuilder query = QueryBuilders.boolQuery().should(addedQuery).should(updatedQuery)
            .should(deletedQuery).filter(QueryBuilders.termsQuery("_uid",commitIds));
        // :TODO you may need a minimum should match
        return query;
    }

    /**
     * Returns the commit history of a element                           (1)
     * <p> Returns a list of commit metadata for the specificed id un-filtered by branch
     * (2)
     * <p>
     *
     * @param nodeId sysmlId     (3)
     * @return JSONArray array or empty json array
     */
    @Override
    public List<Map<String, Object>> elementHistory(String index, String nodeId,
        Set<String> commitIds) throws IOException {
        List<Map<String, Object>> commits = new ArrayList<Map<String, Object>>();
        SearchRequest searchRequest = new SearchRequest();
        QueryBuilder query = getCommitHistoryQuery(nodeId, commitIds);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(query);
        sourceBuilder.size(this.resultLimit);
        sourceBuilder.sort(new FieldSortBuilder("created").order(SortOrder.DESC));
        // :TODO check query output, public SearchSourceBuilder postFilter​(QueryBuilder postFilter)
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits(); //can be null
        for (SearchHit hit : searchHits) {
            Map<String, Object> source = hit.getSourceAsMap();// gets "_source"
            commits.add(source);
        }
        return commits;
    }

}

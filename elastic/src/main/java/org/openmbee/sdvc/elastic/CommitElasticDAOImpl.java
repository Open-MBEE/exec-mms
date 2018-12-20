package org.openmbee.sdvc.elastic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import org.openmbee.sdvc.crud.repositories.commit.CommitIndexDAO;
import org.openmbee.sdvc.json.BaseJson;
import org.openmbee.sdvc.json.CommitJson;
import org.springframework.stereotype.Component;

@Component
public class CommitElasticDAOImpl extends BaseElasticDAOImpl<CommitJson> implements CommitIndexDAO {

    protected CommitJson newInstance() {
        return new CommitJson();
    }

    public void indexAll(Collection<? extends BaseJson> jsons) {
        this.indexAll(getIndex() + "_commit", jsons);
    }

    public void index(BaseJson json) {
        this.index(getIndex() + "_commit", json);
    }

    public Optional<CommitJson> findById(String indexId) {
        return this.findById(getIndex() + "_commit", indexId);
    }

    public List<CommitJson> findAllById(Set<String> indexIds) {
        return this.findAllById(getIndex() + "_commit", indexIds);
    }

    public void deleteById(String indexId) {
        this.deleteById(getIndex() + "_commit", indexId);
    }

    public void deleteAll(Collection<? extends BaseJson> jsons) {
        this.deleteAll(getIndex() + "_commit", jsons);
    }

    public boolean existsById(String indexId) {
        return this.existsById(getIndex() + "_commit", indexId);
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
        QueryBuilder query = QueryBuilders.boolQuery()
            .should(addedQuery)
            .should(updatedQuery)
            .should(deletedQuery)
            .filter(QueryBuilders.termsQuery("id", commitIds))
            .minimumShouldMatch(1);
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
    public List<CommitJson> elementHistory(String nodeId, Set<String> commitIds) {
        try {
            List<CommitJson> commits = new ArrayList<>();
            SearchRequest searchRequest = new SearchRequest(getIndex() + "_commit");
            QueryBuilder query = getCommitHistoryQuery(nodeId, commitIds);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(query);
            //sourceBuilder.size(this.resultLimit); //this caused nothing to return
            sourceBuilder.sort(new FieldSortBuilder("_created").order(SortOrder.DESC));
            // :TODO check query output, public SearchSourceBuilder postFilter​(QueryBuilder postFilter)
            searchRequest.source(sourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits(); //can be null
            for (SearchHit hit : searchHits) {
                Map<String, Object> source = hit.getSourceAsMap();// gets "_source"
                CommitJson ob = newInstance();
                ob.putAll(source);
                commits.add(ob);
            }
            return commits;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

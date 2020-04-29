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
import org.openmbee.sdvc.core.dao.CommitIndexDAO;
import org.openmbee.sdvc.elastic.utils.Index;
import org.openmbee.sdvc.json.BaseJson;
import org.openmbee.sdvc.json.CommitJson;
import org.springframework.stereotype.Component;

@Component
public class CommitElasticDAOImpl extends BaseElasticDAOImpl<CommitJson> implements CommitIndexDAO {

    protected CommitJson newInstance() {
        return new CommitJson();
    }

    public void indexAll(Collection<? extends BaseJson> jsons) {
        this.indexAll(getIndex(), jsons);
    }

    public void index(BaseJson json) {
        this.index(getIndex(), json);
    }

    public Optional<CommitJson> findById(String docId) {
        return this.findById(getIndex(), docId);
    }

    public List<CommitJson> findAllById(Set<String> docIds) {
        return this.findAllById(getIndex(), docIds);
    }

    public void deleteById(String docId) {
        this.deleteById(getIndex(), docId);
    }

    public void deleteAll(Collection<? extends BaseJson> jsons) {
        this.deleteAll(getIndex(), jsons);
    }

    public boolean existsById(String docId) {
        return this.existsById(getIndex(), docId);
    }

    /**
     * Gets the JSON document of a bool : should commit query result printed as Json looks like: {
     * "bool":{"should":[{"term":{"added.id":id}}, {"term":{"updated.id":id}},
     * {"term":{"deleted.id":id}}], "filter": {"terms": {"id": [commitIds]}}} }
     *
     * @param id the nodeId to add to the term search
     * @param commitIds relevant commit ids
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
            .filter(QueryBuilders.termsQuery(CommitJson.ID, commitIds))
            .minimumShouldMatch(1);
        return query;
    }

    /**
     * Returns the commit history of a element
     * <p> Returns a list of commit metadata for the specificed id
     *
     * <p>
     *
     * @param nodeId sysmlId
     * @param commitIds list of commitIds for the relevant branch
     * @return JSONArray array or empty json array
     */
    @Override
    public List<CommitJson> elementHistory(String nodeId, Set<String> commitIds) {
        try {
            List<CommitJson> commits = new ArrayList<>();
            SearchRequest searchRequest = new SearchRequest(getIndex());
            QueryBuilder query = getCommitHistoryQuery(nodeId, commitIds);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(query);
            sourceBuilder.size(this.resultLimit); // TODO handle paging requests
            sourceBuilder.sort(new FieldSortBuilder(CommitJson.CREATED).order(SortOrder.DESC));
            searchRequest.source(sourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            if (hits.getTotalHits().value == 0) {
                return commits;
            }
            for (SearchHit hit : hits.getHits()) {
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

    @Override
    protected String getIndex() {
        return Index.COMMIT.get();
    }
}

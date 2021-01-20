package org.openmbee.mms.elastic;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
import org.openmbee.mms.core.dao.CommitIndexDAO;
import org.openmbee.mms.elastic.utils.Index;
import org.openmbee.mms.json.BaseJson;
import org.openmbee.mms.json.CommitJson;
import org.springframework.stereotype.Component;

@Component
public class CommitElasticDAOImpl extends BaseElasticDAOImpl<CommitJson> implements CommitIndexDAO {

    protected CommitJson newInstance() {
        return new CommitJson();
    }

    public void indexAll(Collection<? extends BaseJson> jsons) {
        this.indexAll(getIndex(), associateCommits(jsons));
    }

    public void index(BaseJson json) {
        if (json.get(CommitJson.COMMITID).toString().isEmpty()) {
            CommitJson commitJson = (CommitJson) json;
            commitJson.put(CommitJson.COMMITID, UUID.randomUUID().toString());
        }
        this.index(getIndex(), json);
    }

    public void index(BaseJson json, String commitId) {
        this.index(getIndex(), addCommitId(json, commitId));
    }

    public Optional<CommitJson> findById(String commitId) {
        return Optional.of(getFullCommit(commitId));
    }

    public List<CommitJson> findAllById(Set<String> commitIds) {
        return getFullCommits(commitIds);
    }

    public void deleteById(String commitId) {
        this.deleteById(getIndex(), commitId);
    }

    public void deleteAll(Collection<? extends BaseJson> jsons) {
        this.deleteAll(getIndex(), jsons);
    }

    public boolean existsById(String commitId) {
        return this.existsById(getIndex(), commitId);
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
            .filter(QueryBuilders.termsQuery(CommitJson.COMMITID, commitIds))
            .minimumShouldMatch(1);
        return query;
    }

    /**
     * Returns the commit history of a element
     * <p> Returns a list of commit metadata for the specified id
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
            QueryBuilder query = getCommitHistoryQuery(nodeId, commitIds);
            SearchHits hits = getCommitResults(query);
            if (hits.getTotalHits().value == 0) {
                return new ArrayList<>();
            }
            LinkedHashMap<String, List<CommitJson>> rawCommits = new LinkedHashMap<>();
            for (SearchHit hit : hits.getHits()) {
                CommitJson ob = new CommitJson();
                ob.putAll(hit.getSourceAsMap());
                if (!rawCommits.containsKey(ob.getCommitId())) {
                    rawCommits.put(ob.getCommitId(), new ArrayList<>());
                }
                rawCommits.get(ob.getCommitId()).add(ob); // gets "_source"
            }
            ArrayList<CommitJson> result = new ArrayList<>();
            for (String key : rawCommits.keySet()) {
                result.add(mungCommits(rawCommits.get(key)));
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getIndex() {
        return Index.COMMIT.get();
    }

    @Override
    public CommitJson update(CommitJson commitJson) {
        return this.update(getIndex(), commitJson);
    }

    private Collection<? extends BaseJson> associateCommits(Collection<? extends BaseJson> jsons) {
        String initialCommitId = jsons.stream().findFirst().orElseThrow().getCommitId();
        return jsons.stream().map(json -> addCommitId(json, initialCommitId)).collect(Collectors.toList());
    }

    private List<CommitJson> getFullCommits(Collection<String> commitIds) {
        return commitIds.stream().map(this::getFullCommit).collect(Collectors.toList());
    }

    private CommitJson getFullCommit(String commitId) {
        try {
            QueryBuilder commitQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.termQuery(CommitJson.COMMITID, commitId))
                .should(QueryBuilders.termQuery(CommitJson.ID, commitId)) // Should it still be supported?
                .minimumShouldMatch(1);
            SearchHits hits = getCommitResults(commitQuery);
            if (hits.getTotalHits().value == 0) {
                return new CommitJson();
            }
            List<CommitJson> rawCommits = new ArrayList<>();
            for (SearchHit hit : hits.getHits()) {
                CommitJson ob = new CommitJson();
                ob.putAll(hit.getSourceAsMap());
                rawCommits.add(ob); // gets "_source"
            }
            return mungCommits(rawCommits);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private SearchHits getCommitResults(QueryBuilder query) throws IOException {
        SearchRequest searchRequest = new SearchRequest(getIndex());
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(query);
        sourceBuilder.size(this.resultLimit); // TODO handle paging requests
        sourceBuilder.sort(new FieldSortBuilder(CommitJson.CREATED).order(SortOrder.DESC));
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return searchResponse.getHits();
    }

    private static CommitJson mungCommits(List<CommitJson> commits) {
        return commits.stream().reduce(new CommitJson(), (partial, raw) -> {
            if (partial.getAdded() == null) {
                partial.setAdded(new ArrayList<>());
            }
            if (partial.getUpdated() == null) {
                partial.setUpdated(new ArrayList<>());
            }
            if (partial.getDeleted() == null) {
                partial.setDeleted(new ArrayList<>());
            }
            if (partial.getSource() == null) {
                partial.setSource("");
            }
            if (partial.getComment() == null) {
                partial.setComment("");
            }
            if (partial.getId() == null) {
                partial.setId("");
            }
            if (partial.getCommitId() == null) {
                partial.setCommitId("");
            }

            if (partial.getAdded().isEmpty() && raw.getAdded() != null) {
                partial.getAdded().addAll(raw.getAdded());
            }
            if (partial.getUpdated().isEmpty() && raw.getUpdated() != null) {
                partial.getUpdated().addAll(raw.getUpdated());
            }
            if (partial.getDeleted().isEmpty() && raw.getDeleted() != null) {
                partial.getDeleted().addAll(raw.getDeleted());
            }
            if (partial.getSource().isEmpty() && raw.getSource() != null) {
                partial.setSource(raw.getSource());
            }
            if (partial.getComment().isEmpty() && raw.getComment() != null) {
                partial.setComment(raw.getComment());
            }
            if (partial.getId().isEmpty() && raw.getCommitId() != null) {
                partial.setId(raw.getCommitId());
            }
            if (partial.getCommitId().isEmpty() && raw.getCommitId() != null) {
                partial.setCommitId(raw.getCommitId());
            }
            return partial;
        });
    }

    @SuppressWarnings("unchecked")
    // Should be the same type?
    private static <T extends BaseJson> T addCommitId(T json, String associationId) {
        return (T) json.put(CommitJson.COMMITID, associationId);
    }
}

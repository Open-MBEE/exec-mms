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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommitElasticDAOImpl extends BaseElasticDAOImpl<CommitJson> implements CommitIndexDAO {

    @Value("${elasticsearch.limit.index}")
    int indexLimit;

    protected CommitJson newInstance() {
        return new CommitJson();
    }

    public void indexAll(Collection<? extends BaseJson> jsons) {
        this.indexAll(getIndex(), jsons);
    }

    public void index(BaseJson json) {
        index((CommitJson) json);
    }

    public void index(CommitJson json) {
        int commitCount = getCommitSize(json);
        List<CommitJson> broken = new ArrayList<>();
        if (commitCount > indexLimit) {
            List<Map<String, Object>> allActions = new ArrayList<>();
            allActions.addAll(json.getAdded().stream().peek(toAdd -> toAdd.put("action", "added")).collect(Collectors.toList()));
            allActions.addAll(json.getUpdated().stream().peek(toUpdate -> toUpdate.put("action", "updated")).collect(Collectors.toList()));
            allActions.addAll(json.getDeleted().stream().peek(toDelete -> toDelete.put("action", "deleted")).collect(Collectors.toList()));

            while (!allActions.isEmpty()) {
                CommitJson currentCommitCopy = CommitJson.copy(json);
                while (getCommitSize(currentCommitCopy) < indexLimit && !allActions.isEmpty()) {
                    Map<String, Object> action = allActions.remove(0);
                    switch (action.getOrDefault("action", "none").toString()) {
                        case "added":
                            currentCommitCopy.getAdded().add(action);
                            break;
                        case "updated":
                            currentCommitCopy.getUpdated().add(action);
                            break;
                        case "deleted":
                            currentCommitCopy.getDeleted().add(action);
                            break;
                    }
                }
                broken.add(currentCommitCopy);

            }
            this.indexAll(broken);

        } else {
            this.index(getIndex(), json);
        }
    }

    public Optional<CommitJson> findById(String commitId) {
        return Optional.of(getFullCommit(commitId));
    }

    public List<CommitJson> findAllById(Set<String> commitIds) {
        return getFullCommits(commitIds);
    }

    public void deleteById(String commitId) {
        List<CommitJson> docs = getDocs(commitId);
        docs.forEach(commit -> {
           this.deleteById(getIndex(), commit.getDocId());
        });
    }

    public void deleteAll(Collection<? extends BaseJson> jsons) {
        this.deleteAll(getIndex(), jsons);
    }

    public boolean existsById(String commitId) {
        List<CommitJson> docs = getDocs(commitId);
        List<Boolean> result = new ArrayList<>();
        docs.forEach(commit -> {
            result.add(this.existsById(getIndex(), commit.getDocId()));
        });
        return !result.stream().allMatch(b -> b);
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
                if (!rawCommits.containsKey(ob.getId())) {
                    rawCommits.put(ob.getId(), new ArrayList<>());
                }
                rawCommits.get(ob.getId()).add(ob); // gets "_source"
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

    private List<CommitJson> getDocs(String commitId) {
        try {
            QueryBuilder commitQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.termQuery(CommitJson.ID, commitId))
                .minimumShouldMatch(1);
            SearchHits hits = getCommitResults(commitQuery);
            if (hits.getTotalHits().value == 0) {
                return new ArrayList<>();
            }
            List<CommitJson> rawCommits = new ArrayList<>();
            for (SearchHit hit : hits.getHits()) {
                CommitJson ob = new CommitJson();
                ob.putAll(hit.getSourceAsMap());
                rawCommits.add(ob); // gets "_source"
            }
            return rawCommits;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private List<CommitJson> getFullCommits(Collection<String> commitIds) {
        return commitIds.stream().map(this::getFullCommit).collect(Collectors.toList());
    }

    private CommitJson getFullCommit(String commitId) {
        return mungCommits(getDocs(commitId));
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
            if (partial.getId() == null) {
                partial.setId("");
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
            if (partial.getId().isEmpty() && raw.getId() != null) {
                partial.setId(raw.getId());
            }
            return partial;
        });
    }

    private static int getCommitSize(CommitJson commitJson) {
        int commitCount = 0;
        if (commitJson.getAdded() != null) {
            commitCount = commitCount + commitJson.getAdded().size();
        }
        if (commitJson.getUpdated() != null) {
            commitCount = commitCount + commitJson.getUpdated().size();
        }
        if (commitJson.getDeleted() != null) {
            commitCount = commitCount + commitJson.getDeleted().size();
        }
        return commitCount;
    }
}

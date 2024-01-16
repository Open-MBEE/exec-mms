package org.openmbee.mms.opensearch;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.update.UpdateRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.script.Script;
import org.opensearch.script.ScriptType;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.sort.FieldSortBuilder;
import org.opensearch.search.sort.SortOrder;
import org.openmbee.mms.core.config.ContextHolder;
import org.openmbee.mms.core.dao.NodeIndexDAO;
import org.openmbee.mms.core.exceptions.InternalErrorException;
import org.openmbee.mms.opensearch.utils.BulkProcessor;
import org.openmbee.mms.opensearch.utils.Index;
import org.openmbee.mms.json.BaseJson;
import org.openmbee.mms.json.ElementJson;
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

    public Optional<ElementJson> findById(String docId) {
        return this.findById(getIndex(), docId);
    }

    public List<ElementJson> findAllById(Set<String> docIds) {
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

    @Override
    public Optional<ElementJson> getByCommitId(String commitId, String nodeId) {
        try {
            SearchRequest searchRequest = new SearchRequest(getIndex());
            // searches the elements for the reference with the current commitId and nodeId
            QueryBuilder query = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(ElementJson.COMMITID, commitId))
                .filter(QueryBuilders.termQuery(ElementJson.ID, nodeId));
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(query);
            searchRequest.source(sourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, REQUEST_OPTIONS);
            if (searchResponse.getHits().getTotalHits().value == 0) {
                return Optional.empty();
            }
            ElementJson ob = newInstance();
            ob.putAll(searchResponse.getHits().getAt(0).getSourceAsMap());
            return Optional.of(ob);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new InternalErrorException(e);
        }
    }

    protected static String ADD_TO_REF = "if(ctx._source.containsKey(\"_inRefIds\")){ctx._source._inRefIds.add(params.refId)} else {ctx._source._inRefIds = [params.refId]}";

    public void addToRef(Set<String> docIds) {
        bulkUpdateRefWithScript(docIds, ADD_TO_REF);
    }

    protected static String REMOVE_FROM_REF = "if(ctx._source.containsKey(\"_inRefIds\")){ctx._source._inRefIds.removeAll([params.refId])}";

    public void removeFromRef(Set<String> docIds) {
        bulkUpdateRefWithScript(docIds, REMOVE_FROM_REF);
    }

    private void bulkUpdateRefWithScript(Set<String> docIds, String script) {
        if (docIds.isEmpty()) {
            return;
        }
        BulkProcessor bulkProcessor = getBulkProcessor(client);
        Map<String, Object> parameters = Collections.singletonMap("refId",
            ContextHolder.getContext().getBranchId());
        for (String docId : docIds) {
            UpdateRequest request = new UpdateRequest(getIndex(), docId);
            Script inline = new Script(ScriptType.INLINE, "painless", script,
                parameters);
            request.script(inline);
            bulkProcessor.add(request);
        }
        bulkProcessor.close();
    }

    @Override
    public Optional<ElementJson> getElementLessThanOrEqualTimestamp(String nodeId,
        String timestamp, List<String> refsCommitIds) {
        int count = 0;
        while (count < refsCommitIds.size()) {
            try {
                List<String> sub = refsCommitIds.subList(count, Math.min(refsCommitIds.size(), count + this.termLimit));
                SearchRequest searchRequest = new SearchRequest(getIndex());
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                // Query
                QueryBuilder query = QueryBuilders.boolQuery()
                    .filter(QueryBuilders
                        .termsQuery(ElementJson.COMMITID, sub))
                    .filter(QueryBuilders.termQuery(ElementJson.ID, nodeId))
                    .filter(QueryBuilders.rangeQuery(ElementJson.MODIFIED).lte(timestamp));
                searchSourceBuilder.query(query);
                searchSourceBuilder.sort(new FieldSortBuilder("_modified").order(SortOrder.DESC));
                searchSourceBuilder.size(1);
                searchRequest.source(searchSourceBuilder);

                SearchResponse searchResponse = client.search(searchRequest, REQUEST_OPTIONS);
                SearchHit[] searchHits = searchResponse.getHits().getHits();
                if (searchHits != null && searchHits.length > 0) {
                    ElementJson elementJson = newInstance();
                    elementJson.putAll(searchHits[0].getSourceAsMap());
                    return Optional.of(elementJson);
                }
                count += this.termLimit;
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new InternalErrorException(e);
            }
        }
        return Optional.empty();
    }

    @Override
    protected String getIndex() {
        return Index.NODE.get();
    }
}


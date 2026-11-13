package org.openmbee.mms.elastic;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.openmbee.mms.core.config.ContextHolder;
import org.openmbee.mms.data.dao.NodeIndexDAO;
import org.openmbee.mms.core.exceptions.InternalErrorException;
import org.openmbee.mms.elastic.utils.BulkProcessor;
import org.openmbee.mms.elastic.utils.ElasticClient;
import org.openmbee.mms.elastic.utils.Index;
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
            // searches the elements for the reference with the current commitId and nodeId
            Map<String, Object> query = Map.of("bool", Map.of("filter", List.of(
                Map.of("term", Map.of(ElementJson.COMMITID, commitId)),
                Map.of("term", Map.of(ElementJson.ID, nodeId)))));
            List<Map<String, Object>> hits = ElasticClient.sources(search(getIndex(), Map.of("query", query)));
            if (hits.isEmpty()) {
                return Optional.empty();
            }
            ElementJson ob = newInstance();
            ob.putAll(hits.get(0));
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
            Map<String, Object> inline = new LinkedHashMap<>();
            inline.put("source", script);
            inline.put("lang", "painless");
            inline.put("params", parameters);
            bulkProcessor.update(getIndex(), docId, Map.of("script", inline));
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
                Map<String, Object> query = Map.of("bool", Map.of("filter", List.of(
                    Map.of("terms", Map.of(ElementJson.COMMITID, sub)),
                    Map.of("term", Map.of(ElementJson.ID, nodeId)),
                    Map.of("range", Map.of(ElementJson.MODIFIED, Map.of("lte", timestamp))))));
                Map<String, Object> searchRequest = new LinkedHashMap<>();
                searchRequest.put("query", query);
                searchRequest.put("sort", List.of(Map.of("_modified", Map.of("order", "desc"))));
                searchRequest.put("size", 1);

                List<Map<String, Object>> searchHits = ElasticClient.sources(search(getIndex(), searchRequest));
                if (!searchHits.isEmpty()) {
                    ElementJson elementJson = newInstance();
                    elementJson.putAll(searchHits.get(0));
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


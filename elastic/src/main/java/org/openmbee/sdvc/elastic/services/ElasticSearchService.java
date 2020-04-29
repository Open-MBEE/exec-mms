package org.openmbee.sdvc.elastic.services;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.openmbee.sdvc.core.config.ContextHolder;
import org.openmbee.sdvc.core.dao.NodeDAO;
import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.openmbee.sdvc.core.services.SearchService;
import org.openmbee.sdvc.data.domains.scoped.Node;
import org.openmbee.sdvc.elastic.utils.Index;
import org.openmbee.sdvc.json.ElementJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class ElasticSearchService implements SearchService {
    @Value("${elasticsearch.limit.result}")
    protected int resultLimit;
    @Value("${elasticsearch.limit.term}")
    protected int termLimit;
    protected static int readTimeout = 1000000000;
    protected RestHighLevelClient client;
    protected NodeDAO nodeRepository;

    @Autowired
    public void setRestHighLevelClient(@Qualifier("clientElastic") RestHighLevelClient client) {
        this.client = client;
    }

    @Autowired
    public void setNodeRepository(NodeDAO nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Override
    public ElementsResponse basicSearch(String projectId, String refId, Map<String, String> params) {
        return recursiveSearch(projectId, refId, params, null);
    }

    @Override
    public ElementsResponse recursiveSearch(String projectId, String refId, Map<String, String> params, Map<String, String> recurse) {
        try {
            ContextHolder.setContext(projectId, refId);
            List<Node> validNodes = nodeRepository.findAllByDeleted(false);
            if(validNodes == null || validNodes.isEmpty()) {
                return new ElementsResponse();
            }

            Map<String, ElementJson> elementJsonMap = new HashMap<>();
            performRecursiveSearch(validNodes, params, recurse, elementJsonMap);
            return prepareResponse(elementJsonMap.values());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void performRecursiveSearch(List<Node> validNodes, Map<String, String> params, Map<String, String> recurse,
                                        Map<String, ElementJson> elementJsonMap) throws IOException {
        SearchHits hits = doSearch(validNodes, params);
        if (hits.getTotalHits().value == 0) {
            return;
        }
        for(SearchHit hit : hits) {
            ElementJson ob = new ElementJson();
            ob.putAll(hit.getSourceAsMap());
            if(! elementJsonMap.containsKey(ob.getId())) {
                elementJsonMap.put(ob.getId(), ob);
                Map<String, String> recursiveParams = buildRecursiveParams(ob, recurse);
                if(! recursiveParams.isEmpty()) {
                    performRecursiveSearch(validNodes, recursiveParams, recurse, elementJsonMap);
                }
            }
        }
    }

    private Map<String, String> buildRecursiveParams(ElementJson ob, Map<String, String> recurse) {
        Map<String, String> recursiveParams = new HashMap<>();

        if(recurse == null || recurse.isEmpty()) {
            return recursiveParams;
        }

        for(Map.Entry<String, String> e : recurse.entrySet()) {
            Object o = ob.get(e.getKey());
            if(o == null) {
                continue;
            }
            recursiveParams.put(e.getValue(), o.toString());
        }
        return recursiveParams;
    }


    private SearchHits doSearch(List<Node> validNodes, Map<String, String> params) throws IOException {
        SearchRequest searchRequest = new SearchRequest(Index.NODE.get());
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.filter(QueryBuilders.termsQuery(ElementJson.DOCID, validNodes.stream().map(Node::getDocId).toArray()));

        for(Map.Entry<String, String> e : params.entrySet()) {
            query.filter(QueryBuilders.termQuery(e.getKey(), e.getValue()));
        }

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(query);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return searchResponse.getHits();
    }

    private ElementsResponse prepareResponse(Collection<ElementJson> result) {
        ElementsResponse response = new ElementsResponse();
        response.setElements(new ArrayList(result));
        return response;
    }
}

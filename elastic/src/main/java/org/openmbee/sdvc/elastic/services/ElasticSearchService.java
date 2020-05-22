package org.openmbee.sdvc.elastic.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.openmbee.sdvc.core.config.ContextHolder;
import org.openmbee.sdvc.core.dao.NodeDAO;
import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.openmbee.sdvc.core.objects.Rejection;
import org.openmbee.sdvc.core.services.SearchService;
import org.openmbee.sdvc.data.domains.scoped.Node;
import org.openmbee.sdvc.elastic.utils.Index;
import org.openmbee.sdvc.json.ElementJson;
import org.openmbee.sdvc.search.SearchConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ElasticSearchService implements SearchService {
    private final Logger logger = LogManager.getLogger(getClass());

    @Value("${elasticsearch.limit.result}")
    protected int resultLimit;
    @Value("${elasticsearch.limit.scrollTimeout}")
    protected long scrollTimeout;
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
        if(params == null || params.isEmpty()) {
            return new ElementsResponse();
        }

        return recursiveSearch(projectId, refId, params, null);
    }

    @Override
    public ElementsResponse recursiveSearch(String projectId, String refId, Map<String, String> params, Map<String, String> recurse) {
        if(params == null || params.isEmpty()) {
            return new ElementsResponse();
        }

        try {
            ContextHolder.setContext(projectId, refId);
            List<Node> allNodes = nodeRepository.findAll();
            if(allNodes == null || allNodes.isEmpty()) {
                return new ElementsResponse();
            }

            Set<String> allNodeDocIds = allNodes.stream().map(Node::getDocId).collect(Collectors.toCollection(HashSet::new));
            Map<String, ElementJson> elementJsonMap = new HashMap<>();
            Collection<Rejection> deletedElements = new HashSet<>();

            boolean showDeletedAsRejected = false;
            String showDeleted = params.remove(SearchConstants.SHOW_DELETED_FIELD);
            if(showDeleted != null && showDeleted.equals("true")) {
                showDeletedAsRejected = true;
            }

            performRecursiveSearch(allNodeDocIds, params, recurse, elementJsonMap);
            Collection<ElementJson> filteredElementJson = filterIndexedElementsUsingDatabaseNodes(allNodes, elementJsonMap, deletedElements, showDeletedAsRejected);
            return prepareResponse(filteredElementJson, deletedElements);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void performRecursiveSearch(Set<String> allNodeDocIds, Map<String, String> params, Map<String, String> recurse,
                                        Map<String, ElementJson> elementJsonMap) throws IOException {
        List<ElementJson> elementJsonList = doSearch(allNodeDocIds, params);
        for(ElementJson ob : elementJsonList) {
            if(!elementJsonMap.containsKey(ob.getId())) {
                elementJsonMap.put(ob.getId(), ob);
                Map<String, String> recursiveParams = buildRecursiveParams(ob, recurse);
                if(!recursiveParams.isEmpty()) {
                    performRecursiveSearch(allNodeDocIds, recursiveParams, recurse, elementJsonMap);
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


    private List<ElementJson> doSearch(Set<String> allNodeDocIds, Map<String, String> params) throws IOException {
        SearchRequest searchRequest = new SearchRequest(Index.NODE.get());
        BoolQueryBuilder query = QueryBuilders.boolQuery();

        for(Map.Entry<String, String> e : params.entrySet()) {
            query.must(QueryBuilders.termQuery(e.getKey(), e.getValue()));
        }

        return performElasticQuery(allNodeDocIds, searchRequest, query);
    }

    private List<ElementJson> performElasticQuery(Set<String> allNodeDocIds, SearchRequest searchRequest, BoolQueryBuilder query) throws IOException {
        List<ElementJson> result = new ArrayList<>();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(query);
        sourceBuilder.size(resultLimit);
        searchRequest.source(sourceBuilder);
        searchRequest.scroll(TimeValue.timeValueMillis(scrollTimeout));
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        String scrollId = null;

        do {
            for(SearchHit hit : searchResponse.getHits()) {
                ElementJson ob = parseResult(hit);
                if(allNodeDocIds.contains(ob.getDocId())) {
                    result.add(ob);
                }
            }
            scrollId = searchResponse.getScrollId();
            if (scrollId != null) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
            }

        } while (scrollId != null && searchResponse.getHits().getHits() != null && searchResponse.getHits().getHits().length != 0);

        return result;
    }

    private ElementJson parseResult(SearchHit hit) {
        ElementJson ob = new ElementJson();
        ob.putAll(hit.getSourceAsMap());
        return ob;
    }

    private Collection<ElementJson> filterIndexedElementsUsingDatabaseNodes(List<Node> allNodes, Map<String, ElementJson> elementJsonMap, Collection<Rejection> deletedElements, boolean showDeletedAsRejected) {
        Collection<ElementJson> filteredIndexedElements = new HashSet<>();

        ElementJson currentJson;
        for(Node n : allNodes) {
            currentJson = elementJsonMap.remove(n.getNodeId());
            if(currentJson != null) {
                if(!n.isDeleted()) {
                    filteredIndexedElements.add(currentJson);
                } else if(showDeletedAsRejected) {
                    deletedElements.add(new Rejection(currentJson, 410, SearchConstants.ELEMENT_DELETED_INFO));
                }
            } else { // node found in DB not found in the index
                logger.warn(SearchConstants.POSSIBLE_ELASTIC_DISCREPANCY, n.getNodeId());
            }
        }

        return filteredIndexedElements;
    }

    private ElementsResponse prepareResponse(Collection<ElementJson> result, Collection<Rejection> rejected) {
        ElementsResponse response = new ElementsResponse();
        response.setElements(new ArrayList(result));
        response.setRejected(new ArrayList(rejected));
        return response;
    }
}

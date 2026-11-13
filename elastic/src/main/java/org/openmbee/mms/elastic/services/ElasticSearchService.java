package org.openmbee.mms.elastic.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmbee.mms.core.config.Constants;
import org.openmbee.mms.core.config.ContextHolder;
import org.openmbee.mms.data.dao.NodeDAO;
import org.openmbee.mms.core.exceptions.InternalErrorException;
import org.openmbee.mms.core.objects.ElementsSearchResponse;
import org.openmbee.mms.core.objects.Rejection;
import org.openmbee.mms.core.services.SearchService;
import org.openmbee.mms.data.domains.scoped.Node;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.openmbee.mms.elastic.utils.ElasticClient;
import org.openmbee.mms.elastic.utils.Index;
import org.openmbee.mms.json.ElementJson;
import org.openmbee.mms.search.SearchConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ElasticSearchService implements SearchService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${elasticsearch.limit.result}")
    protected int resultLimit;
    @Value("${elasticsearch.limit.scrollTimeout}")
    protected long scrollTimeout;
    protected ElasticClient client;
    protected NodeDAO nodeRepository;

    protected ResponseListener listener = new ResponseListener() {
        @Override
        public void onSuccess(Response response) {
            try {
                logger.debug("Cleared: " + client.parse(response).get("num_freed"));
            } catch (IOException e) {
                logger.debug("Cleared scroll");
            }
        }

        @Override
        public void onFailure(Exception e) {
            logger.error("Scroll clear failure: ");
            logger.error(e.getMessage());
        }
    };

    @Autowired
    public void setElasticClient(@Qualifier("clientElastic") ElasticClient client) {
        this.client = client;
    }

    @Autowired
    public void setNodeRepository(NodeDAO nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Override
    public ElementsSearchResponse basicSearch(String projectId, String refId, Map<String, String> params) {
        if(params == null || params.isEmpty()) {
            return new ElementsSearchResponse();
        }

        return recursiveSearch(projectId, refId, params, null, null, null);
    }

    @Override
    public ElementsSearchResponse recursiveSearch(String projectId, String refId, Map<String, String> params, Map<String, String> recurse, Integer from, Integer size) {
        if(params == null || params.isEmpty()) {
            return new ElementsSearchResponse();
        }

        try {
            ContextHolder.setContext(projectId, refId);
            List<Node> allNodes = nodeRepository.findAll();
            if(allNodes == null || allNodes.isEmpty()) {
                return new ElementsSearchResponse();
            }

            Set<String> allNodeDocIds = allNodes.stream().map(Node::getDocId).collect(Collectors.toCollection(HashSet::new));
            Map<String, OrderedResult<ElementJson>> elementJsonMap = new HashMap<>();
            Collection<OrderedResult<Rejection>> deletedElements = new HashSet<>();

            boolean showDeletedAsRejected = false;
            String showDeleted = params.remove(SearchConstants.SHOW_DELETED_FIELD);
            if(showDeleted != null && showDeleted.equals(Constants.TRUE)) {
                showDeletedAsRejected = true;
            }

            Map<String, List<String>> normalizedParams = new HashMap<>();
            params.forEach((k, v) -> {
                List<String> values = new ArrayList<>();
                values.add(v);
                normalizedParams.put(k, values);
            });

            performRecursiveSearch(allNodeDocIds, normalizedParams, recurse, elementJsonMap,0);
            Collection<OrderedResult<ElementJson>> filteredElementJson = filterIndexedElementsUsingDatabaseNodes(allNodes, elementJsonMap, deletedElements, showDeletedAsRejected);
            return prepareResponse(filteredElementJson, deletedElements, from, size);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new InternalErrorException(e);
        }

    }

    private void performRecursiveSearch(Set<String> allNodeDocIds, Map<String, List<String>> params, Map<String, String> recurse,
                                        Map<String, OrderedResult<ElementJson>> elementJsonMap, Integer count) throws IOException {
        Set<String> fields = new HashSet<>(params.keySet());
        if(recurse != null) {
            fields.addAll(recurse.keySet());
        }
        SearchConfiguration searchConfiguration = getSearchConfiguration(fields);
        List<ElementJson> elementJsonList = doSearch(searchConfiguration, allNodeDocIds, params);
        List<ElementJson> obList = new ArrayList<>();
        for(ElementJson ob : elementJsonList) {
            if(!elementJsonMap.containsKey(ob.getId())) {
                elementJsonMap.put(ob.getId(), new OrderedResult<>(ob, count++));
                obList.add(ob);
            }
        }
        Map<String, List<String>> recursiveParams = buildRecursiveParams(obList, recurse);
        if(!recursiveParams.isEmpty()) {
            performRecursiveSearch(allNodeDocIds, recursiveParams, recurse, elementJsonMap, count);
        }
    }

    @SuppressWarnings("unchecked")
    private SearchConfiguration getSearchConfiguration(Set<String> fields) {
        SearchConfiguration searchConfiguration = new SearchConfiguration();
        try {
            Map<String, Object> fieldCaps = client.request("GET",
                ElasticClient.endpoint(Index.NODE.get(), "_field_caps"),
                Map.of("fields", String.join(",", fields)), null);
            Map<String, Map<String, Map<String, Object>>> capsByField =
                (Map<String, Map<String, Map<String, Object>>>) fieldCaps.get("fields");
            for(String field : fields) {
                Map<String, Map<String, Object>> fieldMap = capsByField == null ? null : capsByField.get(field);
                if(fieldMap == null || fieldMap.isEmpty()) {
                    continue;
                }
                Map<String, Object> cap = fieldMap.values().iterator().next();
                searchConfiguration.addField(field, String.valueOf(cap.get("type")),
                    Boolean.TRUE.equals(cap.get("searchable")));
            }
            return searchConfiguration;
        } catch (IOException e) {
            logger.error("Could retrieve field mappings for search configuration", e);
            throw new InternalErrorException("Could not configure search");
        }
    }

    private Map<String, List<String>> buildRecursiveParams(List<ElementJson> obList, Map<String, String> recurse) {
        Map<String, List<String>> recursiveParams = new HashMap<>();

        if(recurse == null || recurse.isEmpty()) {
            return recursiveParams;
        }

        for(Map.Entry<String, String> e : recurse.entrySet()) {
            List<String> oList = new ArrayList<>();
            for (ElementJson ob : obList) {
                Object o = ob.get(e.getKey());
                if (o == null) {
                    continue;
                }
                oList.add(o.toString());
            }
            if (!oList.isEmpty()) {
                recursiveParams.put(e.getValue(), oList);
            }
        }
        return recursiveParams;
    }


    private List<ElementJson> doSearch(SearchConfiguration searchConfiguration, Set<String> allNodeDocIds, Map<String, List<String>> params) throws IOException {
        List<Map<String, Object>> must = new ArrayList<>();

        for(Map.Entry<String, List<String>> e : params.entrySet()) {
            searchConfiguration.addQueryForField(must, e.getKey(), e.getValue());
        }

        Map<String, Object> query = Map.of("bool", must.isEmpty() ? Map.of() : Map.of("must", must));
        return performElasticQuery(allNodeDocIds, Index.NODE.get(), query);
    }

    private List<ElementJson> performElasticQuery(Set<String> allNodeDocIds, String index, Map<String, Object> query) throws IOException {
        List<ElementJson> result = new ArrayList<>();
        String scroll = scrollTimeout + "s";
        Map<String, Object> searchRequest = new LinkedHashMap<>();
        searchRequest.put("query", query);
        searchRequest.put("size", resultLimit);
        Map<String, Object> searchResponse = client.request("POST", ElasticClient.endpoint(index, "_search"),
            Map.of("scroll", scroll), searchRequest);
        String scrollId = null;
        List<Map<String, Object>> hits;
        do {
            for (Map<String, Object> source : ElasticClient.sources(searchResponse)) {
                ElementJson ob = parseResult(source);
                if (allNodeDocIds.contains(ob.getDocId())) {
                    result.add(ob);
                }
            }
            scrollId = (String) searchResponse.get("_scroll_id");
            if (scrollId != null) {
                Map<String, Object> scrollRequest = new LinkedHashMap<>();
                scrollRequest.put("scroll", scroll);
                scrollRequest.put("scroll_id", scrollId);
                searchResponse = client.request("POST", "/_search/scroll", Collections.emptyMap(), scrollRequest);
            }
            hits = ElasticClient.sources(searchResponse);

        } while (scrollId != null && !hits.isEmpty());

        if (scrollId != null) {
            client.requestAsync("DELETE", "/_search/scroll", Map.of("scroll_id", List.of(scrollId)), listener);
        }

        return result;
    }

    private ElementJson parseResult(Map<String, Object> source) {
        ElementJson ob = new ElementJson();
        ob.putAll(source);
        return ob;
    }

    private Collection<OrderedResult<ElementJson>> filterIndexedElementsUsingDatabaseNodes(List<Node> allNodes, Map<String, OrderedResult<ElementJson>> elementJsonMap, Collection<OrderedResult<Rejection>> deletedElements, boolean showDeletedAsRejected) {
        Collection<OrderedResult<ElementJson>> filteredIndexedElements = new HashSet<>();

        OrderedResult<ElementJson> currentJson;
        for(Node n : allNodes) {
            currentJson = elementJsonMap.remove(n.getNodeId());
            if(currentJson != null) {
                if(!n.isDeleted()) {
                    filteredIndexedElements.add(currentJson);
                } else if(showDeletedAsRejected) {
                    deletedElements.add(new OrderedResult<>(new Rejection(currentJson.getWrapped(), 410, SearchConstants.ELEMENT_DELETED_INFO), currentJson.getOrder()));
                }
            }
        }

        return filteredIndexedElements;
    }

    private ElementsSearchResponse prepareResponse(Collection<OrderedResult<ElementJson>> result, Collection<OrderedResult<Rejection>> rejected, Integer from, Integer size) {
        ElementsSearchResponse response = new ElementsSearchResponse();
        response.setTotal(result.size());
        response.setRejectedTotal(rejected.size());
        response.setElements(sortAndTrim(result, from, size));
        response.setRejected(sortAndTrim(rejected, from, size));
        return response;
    }

    private <T> List<T> sortAndTrim(Collection<OrderedResult<T>> collection, Integer from, Integer size) {
        Stream<T> stream = collection.stream()
            .sorted(Comparator.comparingInt(OrderedResult::getOrder))
            .map(OrderedResult::getWrapped);

        if(from != null) {
            stream = stream.skip(from);
        }
        if(size != null) {
            stream = stream.limit(size);
        }
        return stream.collect(Collectors.toList());
    }

    private static class OrderedResult<T> {
        private final T wrapped;
        private final int order;

        public OrderedResult(T wrapped, int order) {
            this.wrapped = wrapped;
            this.order = order;
        }

        public T getWrapped() {
            return wrapped;
        }

        public int getOrder() {
            return order;
        }
    }
}

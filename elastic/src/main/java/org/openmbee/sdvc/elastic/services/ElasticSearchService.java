package org.openmbee.sdvc.elastic.services;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        try {
            ContextHolder.setContext(projectId, refId);
            List<Node> validNodes = nodeRepository.findAllByDeleted(false);
            if(validNodes == null || validNodes.isEmpty()) {
                return new ElementsResponse();
            }

            SearchHits hits = doSearch(validNodes, params);
            if (hits.getTotalHits().value == 0) {
                return new ElementsResponse();
            }

            List<ElementJson> result = parseResults(hits);
            return prepareResponse(result);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ElementJson> parseResults(SearchHits hits) {
        return Arrays.stream(hits.getHits()).map(h -> {
            ElementJson ob = new ElementJson();
            ob.putAll(h.getSourceAsMap());
            return ob;
        }).collect(Collectors.toList());
    }

    private SearchHits doSearch(List<Node> validNodes, Map<String, String> params) throws IOException {
        SearchRequest searchRequest = new SearchRequest(Index.NODE.get());
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.filter(QueryBuilders.termsQuery(ElementJson.DOCID, validNodes.stream().map(v -> v.getDocId()).toArray()));

        for(Map.Entry<String,String> e : params.entrySet()) {
            query.filter(QueryBuilders.termQuery(e.getKey(), e.getValue()));
        }

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(query);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return searchResponse.getHits();
    }

    private ElementsResponse prepareResponse(List<ElementJson> result) {
        ElementsResponse response = new ElementsResponse();
        response.setElements(result);
        return response;
    }
}

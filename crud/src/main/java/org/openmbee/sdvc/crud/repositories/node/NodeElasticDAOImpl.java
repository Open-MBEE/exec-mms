package org.openmbee.sdvc.crud.repositories.node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.openmbee.sdvc.crud.repositories.BaseElasticDAOImpl;
import org.openmbee.sdvc.json.BaseJson;
import org.springframework.stereotype.Component;

@Component
public class NodeElasticDAOImpl extends BaseElasticDAOImpl implements NodeIndexDAO {

    public Map<String, Object> findByIndexId(String indexId) {
        return null;
    }

    public List<Map<String, Object>> findByIndexIds(Set<String> indexIds) {
        return null;
    }

    public void indexAll(Collection<? extends BaseJson> jsons) throws IOException  {
        this.indexAll("projectId_node", jsons);

    }

    public void index(BaseJson json) throws IOException {
        this.index("projectId_node", json);
    }

    public Map<String, Object> findById(String indexId) throws IOException  {
        return this.findById("projectId_node", indexId);
    }

    public List<Map<String, Object>> findAllById(Set<String> indexIds) throws IOException {
        return this.findAllById("projectId_node", indexIds);
    }

    public void deleteById(String indexId) throws IOException{
        this.deleteById("projectId_node", indexId);
    }

    public void deleteAll(Collection<? extends BaseJson> jsons) throws IOException{
        this.deleteAll("projectId_node", jsons);
    }

    public boolean existsById(String indexId)throws IOException{
        return this.existsById("projectId_node", indexId);
    }
    @Override
    public Map<String, Object> getByCommitId(String commitIndexId, String nodeId, String index) throws IOException {
        Map<String, Object> commits;
        SearchRequest searchRequest = new SearchRequest();
        // searches the elements for the reference with the current commitId (elasticId) and sysmlid Id
        QueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("commitId",commitIndexId)).must(QueryBuilders.termQuery("id",nodeId));
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(query);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        commits = searchResponse.getHits().getAt(0).getSourceAsMap();
        return commits;
    }
}


package org.openmbee.sdvc.crud.repositories;

import java.io.IOException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ProjectElasticImpl implements ProjectIndex {

    protected RestHighLevelClient client;

    @Autowired
    public void setRestHighLevelClient(@Qualifier("clientElastic") RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public void create(String index) throws IOException {
        CreateIndexRequest commitIndex = new CreateIndexRequest(index + "_commit");
        commitIndex.mapping("_doc", getCommitMapAsString(), XContentType.JSON);
        CreateIndexRequest nodeIndex = new CreateIndexRequest(index + "_node");
        createIndex(commitIndex);
        createIndex(nodeIndex);
    }

    private void createIndex(CreateIndexRequest request) throws IOException {
        client.indices().create(request, RequestOptions.DEFAULT).isAcknowledged();
    }

    @Override
    public void delete(String index) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        client.indices().delete(request, RequestOptions.DEFAULT).isAcknowledged();
    }

    private String getCommitMapAsString() {
        return "{\"_doc\"{\"properties\":{\"added\":{\"properties\":{\"id\":{\"type\":\"keyword\"},\"_indexId\":{\"type\":\"keyword\"},\"type\":{\"type\":\"keyword\"}}},\"updated\":{\"properties\":{\"id\":{\"type\":\"keyword\"},\"_indexId\":{\"type\":\"keyword\"},\"previousIndexId\":{\"type\":\"keyword\"},\"type\":{\"type\":\"keyword\"}}},\"deleted\":{\"properties\":{\"id\":{\"type\":\"keyword\"},\"previousIndexId\":{\"type\":\"keyword\"},\"type\":{\"type\":\"keyword\"}}},\"id\":{\"type\":\"keyword\"},\"_refId\":{\"type\":\"keyword\"},\"_creator\":{\"type\":\"keyword\"},\"_created\":{\"type\":\"date\",\"format\":\"yyyy-MM-dd'T'HH:mm:ss.SSSZ\"},\"_projectId\":{\"type\":\"keyword\"},\"_indexId\":{\"type\":\"keyword\"}}}}";
    }
}

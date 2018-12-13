package org.openmbee.sdvc.crud.repositories;

import java.io.IOException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ProjectElasticImpl implements ProjectIndex {

    protected RestHighLevelClient client;

    @Autowired
    public void setRestHighLevelClient(@Qualifier("clientElastic") RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public void create(String index) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(index);
        client.indices().create(request, RequestOptions.DEFAULT).isAcknowledged();
    }

    @Override
    public void delete(String index) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        client.indices().delete(request, RequestOptions.DEFAULT).isAcknowledged();
    }
}

package org.openmbee.sdvc.crud.config;

import org.apache.http.HttpHost;
import org.openmbee.sdvc.core.config.PersistenceJPAConfig;
import org.openmbee.sdvc.core.domains.Project;
import org.openmbee.sdvc.core.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClient;


@Configuration public class ElasticsearchConfig {

    @Value("${elasticsearch.host}") private String elasticsearchHost;

    @Bean(destroyMethod = "close") public RestHighLevelClient restClient() {
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(elasticsearchHost)));
        // :TODO can pass other params to config here, like loading the schema..to be over thought later
        return client;
    }

}
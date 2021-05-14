package org.openmbee.mms.elastic.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.host}")
    private String elasticsearchHost;
    @Value("${elasticsearch.port}")
    private int elasticsearchPort;
    @Value("${elasticsearch.http}")
    private String elasticsearchHttp;

    @Bean(name = "clientElastic", destroyMethod = "close")
    public RestHighLevelClient restClient() {

        RestClientBuilder builder = RestClient.builder(new HttpHost(elasticsearchHost, elasticsearchPort, elasticsearchHttp));
        builder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(10000).setSocketTimeout(1000000));
        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }

}
package org.openmbee.mms.opensearch.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenSearchConfig {

    @Value("${opensearch.host}")
    private String opensearchHost;
    @Value("${opensearch.port}")
    private int opensearchPort;
    @Value("${opensearch.http}")
    private String opensearchHttp;

    @Value("${opensearch.password}")
    private String opensearchPassword;
    @Value("${opensearch.username}")
    private String opensearchUsername;

    @Bean(name = "clientElastic", destroyMethod = "close")
    public RestHighLevelClient restClient() {

        

        RestClientBuilder builder = RestClient.builder(new HttpHost(opensearchHost, opensearchPort, opensearchHttp));
        builder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(10000).setSocketTimeout(1000000));

        if (! opensearchPassword.isEmpty() && ! opensearchUsername.isEmpty()) {
            final CredentialsProvider credentialsProvider =
                new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(opensearchUsername, opensearchPassword));
            builder.setHttpClientConfigCallback(new HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(
                    HttpAsyncClientBuilder httpClientBuilder) {
                return httpClientBuilder
                    .setDefaultCredentialsProvider(credentialsProvider);
            }
        });
        }
        

        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }

}
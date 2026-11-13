package org.openmbee.mms.elastic.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;

/**
 * JSON over the REST API that Elasticsearch and OpenSearch share. Nothing here depends on which of
 * the two is listening, so the server is chosen by the configured host alone.
 */
public class ElasticClient implements Closeable {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};
    private static final ContentType NDJSON = ContentType.create("application/x-ndjson", StandardCharsets.UTF_8);
    private static final RequestOptions REQUEST_OPTIONS;
    static {
        RequestOptions.Builder requestBuilder = RequestOptions.DEFAULT.toBuilder();
        // TODO: Should be configureable
        requestBuilder.setHttpAsyncResponseConsumerFactory(
            new HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory(1024 * 1024 * 1024));
        REQUEST_OPTIONS = requestBuilder.build();
    }

    private final RestClient restClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public ElasticClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public static String endpoint(String... segments) {
        StringBuilder path = new StringBuilder();
        for (String segment : segments) {
            path.append('/').append(URLEncoder.encode(segment, StandardCharsets.UTF_8).replace("+", "%20"));
        }
        return path.toString();
    }

    public Map<String, Object> request(String method, String endpoint) throws IOException {
        return request(method, endpoint, Collections.emptyMap(), null);
    }

    public Map<String, Object> request(String method, String endpoint, Map<String, String> parameters, Object body)
        throws IOException {
        return parse(restClient.performRequest(build(method, endpoint, parameters, body)));
    }

    /**
     * Same as {@link #request}, but a 404 returns the body the server sent instead of throwing.
     */
    public Map<String, Object> requestAllowingNotFound(String method, String endpoint, Map<String, String> parameters,
        Object body) throws IOException {
        try {
            return request(method, endpoint, parameters, body);
        } catch (ResponseException e) {
            if (e.getResponse().getStatusLine().getStatusCode() == 404) {
                return parse(e.getResponse());
            }
            throw e;
        }
    }

    public boolean exists(String endpoint, Map<String, String> parameters) throws IOException {
        Response response = restClient.performRequest(build("HEAD", endpoint, parameters, null));
        return response.getStatusLine().getStatusCode() == 200;
    }

    public Map<String, Object> bulk(String actions) throws IOException {
        Request request = build("POST", "/_bulk", Collections.emptyMap(), null);
        request.setEntity(new NStringEntity(actions, NDJSON));
        return parse(restClient.performRequest(request));
    }

    public void requestAsync(String method, String endpoint, Object body, ResponseListener listener)
        throws IOException {
        restClient.performRequestAsync(build(method, endpoint, Collections.emptyMap(), body), listener);
    }

    public String toJson(Object value) throws IOException {
        return mapper.writeValueAsString(value);
    }

    public Map<String, Object> parse(Response response) throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            return Collections.emptyMap();
        }
        try (InputStream content = entity.getContent()) {
            return mapper.readValue(content, MAP_TYPE);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> sources(Map<String, Object> searchResponse) {
        List<Map<String, Object>> sources = new ArrayList<>();
        Object hits = searchResponse.get("hits");
        if (hits instanceof Map) {
            Object list = ((Map<String, Object>) hits).get("hits");
            if (list instanceof List) {
                for (Map<String, Object> hit : (List<Map<String, Object>>) list) {
                    sources.add((Map<String, Object>) hit.get("_source"));
                }
            }
        }
        return sources;
    }

    private Request build(String method, String endpoint, Map<String, String> parameters, Object body)
        throws IOException {
        Request request = new Request(method, endpoint);
        request.addParameters(parameters);
        request.setOptions(REQUEST_OPTIONS);
        if (body != null) {
            request.setEntity(new NStringEntity(toJson(body), ContentType.APPLICATION_JSON));
        }
        return request;
    }

    @Override
    public void close() throws IOException {
        restClient.close();
    }
}

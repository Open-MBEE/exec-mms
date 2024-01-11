package org.openmbee.mms.elastic.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.openmbee.mms.core.config.ContextHolder;
import org.openmbee.mms.core.exceptions.InternalErrorException;
import org.openmbee.mms.elastic.utils.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@Tag(name = "Elasticsearch")
public class ElasticsearchController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    RestClient client;

    @Autowired
    public ElasticsearchController(RestClient client) {
        this.client = client;
    }

    enum Type {elements, commits};

    @PostMapping(value = "/projects/{projectId}/elasticsearch/{type}")
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_READ', true)")
    public ResponseEntity<StreamingResponseBody> searchProject(
        @PathVariable String projectId,
        @PathVariable Type type,
        @RequestBody String query,
        @RequestParam(required = false) Map<String, String> params) {

        ContextHolder.setContext(projectId);
        String index = type == Type.elements ? Index.NODE.get() : Index.COMMIT.get();
        Request req = new Request("GET", "/" + index + "/_search" );
        req.addParameters(params);
        req.setJsonEntity(query);
        try {
            Response res = client.performRequest(req);
            StreamingResponseBody stream = outputStream -> {
                res.getEntity().getContent().transferTo(outputStream);
                res.getEntity().getContent().close();
                outputStream.close();
            };
            return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(stream);
        } catch (IOException e) {
            logger.error("elasticsearch passthru error", e);
            throw new InternalErrorException(e.getMessage());
        }
    }
}

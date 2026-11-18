package org.openmbee.mms.elastic;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import org.openmbee.mms.core.config.ContextHolder;
import org.openmbee.mms.data.dao.ProjectIndex;
import org.openmbee.mms.core.exceptions.InternalErrorException;
import org.openmbee.mms.elastic.utils.ElasticClient;
import org.openmbee.mms.json.ProjectJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class ProjectElasticImpl extends BaseElasticDAOImpl<ProjectJson> implements ProjectIndex {

    protected ProjectJson newInstance() {
        return new ProjectJson();
    }

    @Autowired
    public void setElasticClient(@Qualifier("clientElastic") ElasticClient client) {
        this.client = client;
    }

    @Override
    public void create(String projectId) {
        ProjectJson projectJson = newInstance();
        projectJson.setProjectId(projectId);
        projectJson.setType("default");
        create(projectJson);
    }

    @Override
    public void create(ProjectJson project) {
        try {
            String projectId = project.getProjectId();
            String projectType = project.getProjectType();
            String index = projectId.toLowerCase();
            createIndex(index + "_commit", getCommitMapping());
            createIndex(index + "_node", getNodeMapping(projectType));
            createIndex(index + "_metadata", getMetadataMapping());
            update(project);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new InternalErrorException(e);
        }
    }

    public Optional<ProjectJson> findById(String docId) {
        return this.findById(getIndex(), docId);
    }

    public List<ProjectJson> findAllById(Set<String> docIds) {
        return this.findAllById(getIndex(), docIds);
    }

    private void createIndex(String index, String mapping) throws IOException {
        client.request("PUT", ElasticClient.endpoint(index), Collections.emptyMap(),
            Map.of("mappings", new ObjectMapper().readTree(mapping)));
    }

    @Override
    public void delete(String projectId) {
        try {
            client.request("DELETE", ElasticClient.endpoint(projectId.toLowerCase() + "_node"));
            client.request("DELETE", ElasticClient.endpoint(projectId.toLowerCase() + "_commit"));
            client.request("DELETE", ElasticClient.endpoint(projectId.toLowerCase() + "_metadata"));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new InternalErrorException(e);
        }
    }

    private String getCommitMapping() throws IOException {
        InputStream in = new ClassPathResource("elastic_mappings/commit.json")
            .getInputStream();
        return read(in);
    }

    private String getMetadataMapping() throws IOException {
        InputStream in = new ClassPathResource("elastic_mappings/metadata.json")
            .getInputStream();
        return read(in);
    }

    private String getNodeMapping(String type) throws IOException {
        InputStream in = new ClassPathResource("elastic_mappings/" + type + "_node.json")
            .getInputStream();
        if (in == null) {
            in = new ClassPathResource("elastic_mappings/default_node.json")
                .getInputStream();
        }
        return read(in);
    }

    private String read(InputStream in) throws IOException {
        if (in == null) {
            throw new IOException("Resource not found!");
        }
        String text;
        try (Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {
            text = scanner.useDelimiter("\\A").next();
        }
        return text;
    }

    @Override
    public ProjectJson update(ProjectJson json) {
        return this.update(getIndex(), json);
    }

    @Override
    protected String getIndex() {
        if (ContextHolder.getContext().getProjectId() == null) {
            return null;
        }
        return super.getIndex() + "_metadata";
    }
}

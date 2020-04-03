package org.openmbee.sdvc.rdb.datasources;

import org.openmbee.sdvc.data.domains.global.Project;
import org.openmbee.sdvc.rdb.config.PersistenceJPAConfig;
import org.openmbee.sdvc.rdb.exceptions.RdbException;
import org.openmbee.sdvc.rdb.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.openmbee.sdvc.core.config.ContextObject.DEFAULT_PROJECT;

public class CrudDataSources {
    private Environment env;
    private ProjectRepository projectRepository;
    private Map<String, DataSource> targetDataSources = new HashMap<>();

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public CrudDataSources() {

        DataSource defaultDs = PersistenceJPAConfig
            .buildDatasource(env.getProperty("spring.datasource.url") + "/" +
                    env.getProperty("spring.datasource.database"),
                env.getProperty("spring.datasource.username"),
                env.getProperty("spring.datasource.password"),
                env.getProperty("spring.datasource.driver-class-name",
                    "org.postgresql.Driver"));

        targetDataSources.put(DEFAULT_PROJECT, defaultDs);

        List<Project> projects = projectRepository.findAll();

        for (Project project : projects) {
            addDataSource(project);
        }
    }

    public DataSource getDataSource(String projectId) {
        DataSource dataSource = targetDataSources.get(projectId);
        if(dataSource == null) {
            dataSource= addDataSource(projectId);
        }
        return dataSource;
    }

    public DataSource getDataSource(Project project) {
        DataSource dataSource = targetDataSources.get(project.getProjectId());
        if(dataSource == null) {
            dataSource= addDataSource(project);
        }
        return dataSource;
    }

    public DataSource refreshDataSource(String projectId) {
        removeDataSource(projectId);
        return addDataSource(projectId);
    }

    public boolean removeDataSource(String projectId) {
        return targetDataSources.remove(projectId) != null;
    }

    private DataSource addDataSource(String projectId) {
        Optional<Project> project = projectRepository.findByProjectId(projectId);

        if(! project.isPresent()) {
            throw new RdbException("Project id " + projectId + " not found");
        }

        return addDataSource(project.get());
    }

    private DataSource addDataSource(Project project) {
        String url = project.getConnectionString();
        if (url == null || url.isEmpty()) {
            url = env.getProperty("spring.datasource.url") + "/_" + project.getProjectId();
        }

        DataSource dataSource = PersistenceJPAConfig.buildDatasource(url,
                env.getProperty("spring.datasource.username"),
                env.getProperty("spring.datasource.password"),
                env.getProperty("spring.datasource.driver-class-name",
                    "org.postgresql.Driver"));

        targetDataSources.put(project.getProjectId(), dataSource);
        return dataSource;
    }
}

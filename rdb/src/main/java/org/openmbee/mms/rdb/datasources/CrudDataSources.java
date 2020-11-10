package org.openmbee.mms.rdb.datasources;

import org.openmbee.mms.data.domains.global.Project;
import org.openmbee.mms.rdb.config.PersistenceJPAConfig;
import org.openmbee.mms.rdb.exceptions.RdbException;
import org.openmbee.mms.rdb.repositories.ProjectRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.openmbee.mms.core.config.ContextObject.DEFAULT_PROJECT;

public class CrudDataSources implements InitializingBean {
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

    public CrudDataSources() {}

    @Override
    public void afterPropertiesSet() throws Exception {
        DataSource defaultDs = buildDataSource(env.getProperty("spring.datasource.url") + "/" +
            env.getProperty("spring.datasource.database"));
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

    public DataSource addDataSource(Project project) {
        String url = project.getConnectionString();
        String prefix = env.getProperty("rdb.project.prefix", "");
        if (url == null || url.isEmpty()) {
            url = env.getProperty("spring.datasource.url") + "/" + prefix + "_" + project.getProjectId();
        }
        DataSource dataSource = buildDataSource(url);
        targetDataSources.put(project.getProjectId(), dataSource);
        return dataSource;
    }

    private DataSource addDataSource(String projectId) {
        Optional<Project> project = projectRepository.findByProjectId(projectId);
        if(! project.isPresent()) {
            throw new RdbException("Project id " + projectId + " not found");
        }
        return addDataSource(project.get());
    }

    private DataSource buildDataSource(String url) {
        return PersistenceJPAConfig.buildDatasource(url,
            env.getProperty("spring.datasource.username"),
            env.getProperty("spring.datasource.password"),
            env.getProperty("spring.datasource.driver-class-name",
                "org.postgresql.Driver"));
    }
}

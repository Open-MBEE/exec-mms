package org.openmbee.sdvc.rdb.config;

import static org.openmbee.sdvc.rdb.config.ContextObject.DEFAULT_PROJECT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.openmbee.sdvc.core.config.PersistenceJPAConfig;
import org.openmbee.sdvc.data.domains.Project;
import org.openmbee.sdvc.core.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RoutingJDBCConfig {

    private Environment env;
    private ProjectRepository projectRepository;

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Bean(name = "crudDataSources")
    public Map<String, DataSource> crudDataSources() {
        Map<String, DataSource> targetDataSources = new HashMap<>();

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
            String url = project.getConnectionString();
            if (url == null || url.isEmpty()) {
                url = env.getProperty("spring.datasource.url") + "/_" + project.getProjectId();
            }
            targetDataSources.put(project.getProjectId(), PersistenceJPAConfig
                .buildDatasource(url,
                    env.getProperty("spring.datasource.username"),
                    env.getProperty("spring.datasource.password"),
                    env.getProperty("spring.datasource.driver-class-name",
                        "org.postgresql.Driver")));
        }

        return targetDataSources;
    }
}

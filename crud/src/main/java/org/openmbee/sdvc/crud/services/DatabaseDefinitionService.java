package org.openmbee.sdvc.crud.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.openmbee.sdvc.core.config.PersistenceJPAConfig;
import org.openmbee.sdvc.core.domains.Project;
import org.openmbee.sdvc.crud.config.CurrentTenantIdentifierResolverImpl;
import org.openmbee.sdvc.crud.config.DataSourceBasedMultiTenantConnectionProviderImpl;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.domains.Branch;
import org.openmbee.sdvc.crud.domains.Commit;
import org.openmbee.sdvc.crud.domains.Edge;
import org.openmbee.sdvc.crud.domains.EdgeType;
import org.openmbee.sdvc.crud.domains.Node;
import org.openmbee.sdvc.crud.domains.NodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

@Service
public class DatabaseDefinitionService {

    private static final Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
    private static final String COPY_SQL = "INSERT INTO %s SELECT * FROM %s";
    protected final Logger logger = LogManager.getLogger(getClass());
    private EntityManager entityManager;
    private Map<String, DataSource> crudDataSources;
    private Environment env;

    private static String substituteVariables(String template, Map<String, String> variables) {
        StringBuffer buffer = new StringBuffer();
        Matcher matcher = pattern.matcher(template);

        while (matcher.find()) {
            if (variables.containsKey(matcher.group(1))) {
                String replacement = variables.get(matcher.group(1));
                matcher.appendReplacement(buffer,
                    replacement != null ? Matcher.quoteReplacement(replacement) : "null");
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    @Autowired
    public void setCrudDataSources(
        @Qualifier("crudDataSources") Map<String, DataSource> crudDataSources) {
        this.crudDataSources = crudDataSources;
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public boolean createProjectDatabase(Project project) throws SQLException {
        DbContextHolder.setContext(null);
        String queryString = String.format("CREATE DATABASE _%s", project.getProjectId());
        JdbcTemplate jdbcTemplate = new JdbcTemplate(
            crudDataSources.get(DbContextHolder.getContext().getKey()));
        List<Object> created = new ArrayList<>();
        try {
            jdbcTemplate.execute(queryString);
            created.add("Created Database");
            generateProjectSchemaFromModels(project);
            created.add("Created Tables");
        } catch (DataAccessException e) {
            if (e.getCause().getLocalizedMessage().toLowerCase().contains("exists")) {
                generateProjectSchemaFromModels(project);
                throw (new SQLException("Database already exists"));
            } else {
                logger.error(e.getLocalizedMessage());
                throw (e);
            }
        }
        return !created.isEmpty();
    }

    public boolean createBranch() {
        logger.error(
            "Current Context is: {} on ref {}", DbContextHolder.getContext().getProjectId(),
            DbContextHolder.getContext().getBranchId());
        generateBranchSchemaFromModels();
        return true;
    }

    public void generateProjectSchemaFromModels(Project project) {

        String connectionString = project.getConnectionString();
        if (connectionString == null || connectionString.equals("")) {
            connectionString =
                env.getProperty("spring.datasource.url") + "/_" + project.getProjectId();
        }

        DataSource ds = PersistenceJPAConfig
            .buildDatasource(connectionString,
                env.getProperty("spring.datasource.username"),
                env.getProperty("spring.datasource.password"),
                env.getProperty("spring.datasource.driver-class-name",
                    "org.postgresql.Driver"));

        this.crudDataSources.put(project.getProjectId(), ds);

        DbContextHolder.setContext(project.getProjectId());

        Map<String, Object> props = entityManager.getEntityManagerFactory().getProperties();

        MetadataSources metadata = new MetadataSources(
            new StandardServiceRegistryBuilder()
                .applySettings(props)
                .build()
        );

        metadata.addAnnotatedClass(Branch.class);
        metadata.addAnnotatedClass(Commit.class);
        metadata.addAnnotatedClass(Edge.class);
        metadata.addAnnotatedClass(EdgeType.class);
        metadata.addAnnotatedClass(Node.class);
        metadata.addAnnotatedClass(NodeType.class);

        File tempFile = null;
        try {
            tempFile = File.createTempFile("project-schema", ".sql");

            new SchemaExport()
                .setOutputFile(tempFile.getAbsolutePath())
                .setHaltOnError(false)
                .setFormat(true)
                .setDelimiter(";")
                .createOnly(EnumSet.of(TargetType.SCRIPT),
                    metadata.getMetadataBuilder().build());

            String projectData = "";
            BufferedReader br = null;

            FileSystemResource schemaResource = new FileSystemResource(tempFile);
            ClassPathResource dataResource = new ClassPathResource("project-data.sql");

            try {
                br = new BufferedReader(
                    new InputStreamReader(dataResource.getInputStream(), Charset.defaultCharset()));

                String rawString = br.lines().collect(Collectors.joining(System.lineSeparator()));
                Map<String, String> substitutions = new HashMap<>();
                substitutions.put("projectId", project.getProjectId());
                projectData = substituteVariables(rawString, substitutions);
            } catch (IOException ioe) {
                logger.debug(ioe);
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException ioe) {
                        logger.debug(ioe);
                    }
                }
            }

            ResourceDatabasePopulator populator = new ResourceDatabasePopulator(schemaResource,
                new ByteArrayResource(projectData.getBytes()));
            populator.execute(ds);
        } catch (IOException e) {

        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }

    public void generateBranchSchemaFromModels() {
        MetadataSources metadata = new MetadataSources(
            new StandardServiceRegistryBuilder()
                .applySettings(getSchemaProperties())
                .build()
        );

        metadata.addAnnotatedClass(Edge.class);
        metadata.addAnnotatedClass(Node.class);

        new SchemaExport()
            .setHaltOnError(true)
            .setFormat(true)
            .setDelimiter(";")
            .createOnly(EnumSet.of(TargetType.DATABASE),
                metadata.getMetadataBuilder().build());
    }

    public void copyTablesFromParent(String target, String parent, String parentCommit) {
        //TODO target and parent are not sanitized
        if (parentCommit != null) {
            return;
        }

        final String targetNodeTable = String.format("nodes%s", target);
        final String targetEdgeTable = String.format("edges%s", target);
        StringBuilder parentNodeTable = new StringBuilder("nodes");
        StringBuilder parentEdgeTable = new StringBuilder("edges");

        if (parent != null && !parent.equalsIgnoreCase("master")) {
            parentNodeTable.append(String.format("%s", parent));
            parentEdgeTable.append(String.format("%s", parent));
        }

        JdbcTemplate jdbcTemplate = new JdbcTemplate(
            crudDataSources.get(DbContextHolder.getContext().getKey()));
        jdbcTemplate.execute(String.format(COPY_SQL, targetNodeTable, parentNodeTable));
        jdbcTemplate.execute(String.format(COPY_SQL, targetEdgeTable, parentEdgeTable));
    }

    private Map<String, Object> getSchemaProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties
            .put("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect",
                "org.hibernate.dialect.PostgreSQL94Dialect"));

        properties.put("hibernate.jdbc.lob.non_contextual_creation", true);
        properties.put("hibernate.current_session_context_class", "thread");
        properties.put("hibernate.multiTenancy", MultiTenancyStrategy.DATABASE);

        DataSourceBasedMultiTenantConnectionProviderImpl dataSourceProvider = new DataSourceBasedMultiTenantConnectionProviderImpl();
        dataSourceProvider.setCrudDataSources(crudDataSources);
        properties.put("hibernate.multi_tenant_connection_provider", dataSourceProvider);

        CurrentTenantIdentifierResolverImpl identifierResolver = new CurrentTenantIdentifierResolverImpl();
        properties.put("hibernate.tenant_identifier_resolver", identifierResolver);

        properties.put("hibernate.physical_naming_strategy",
            "org.openmbee.sdvc.crud.config.SuffixedPhysicalNamingStrategy");

        return properties;
    }
}

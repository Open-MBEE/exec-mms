package org.openmbee.sdvc.rdb.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.openmbee.sdvc.data.domains.global.Project;
import org.openmbee.sdvc.core.config.ContextHolder;
import org.openmbee.sdvc.data.domains.scoped.Branch;
import org.openmbee.sdvc.data.domains.scoped.Commit;
import org.openmbee.sdvc.data.domains.scoped.Node;
import org.openmbee.sdvc.data.domains.scoped.NodeType;
import org.openmbee.sdvc.rdb.datasources.CrudDataSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DatabaseDefinitionService {

    private static final Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
    private static final String COPY_SQL = "INSERT INTO \"%s\" SELECT * FROM \"%s\"";
    private static final String COPY_IDX = "SELECT SETVAL('%s_id_seq', COALESCE((SELECT MAX(id) FROM \"%s\"), 1), true)";

    private static final String INITIAL_REF = "INSERT INTO branches (id, branchid, branchname, tag, deleted, timestamp) VALUES (0, 'master', 'master', false, false, NOW());";

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private CrudDataSources crudDataSources;
    private Environment env;

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    @Autowired
    public void setCrudDataSources(CrudDataSources crudDataSources) {
        this.crudDataSources = crudDataSources;
    }

    public boolean createProjectDatabase(Project project) throws SQLException {
        ContextHolder.setContext(null);
        //TODO sanitize projectId? or reject if not valid id
        JdbcTemplate jdbcTemplate = new JdbcTemplate(
            crudDataSources.getDataSource(ContextHolder.getContext().getKey()));
        List<Object> created = new ArrayList<>();
        try {
            jdbcTemplate.execute("CREATE DATABASE " + databaseProjectString(project));
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

    public void deleteProjectDatabase(Project project) throws SQLException {
        Connection connection = crudDataSources.getDataSource("DEFAULT").getConnection();
        connection.createStatement().executeUpdate(connection.nativeSQL("DROP DATABASE " + databaseProjectString(project)));
    }

    private String databaseProjectString(Project project) {
        return String.format("\"_%s\"", project.getProjectId());
    }

    public void createBranch() {
        logger.info(
            "Current Context is: {} on ref {}", ContextHolder.getContext().getProjectId(),
            ContextHolder.getContext().getBranchId());
        generateBranchSchemaFromModels();
    }

    public void generateProjectSchemaFromModels(Project project) throws SQLException {
        crudDataSources.addDataSource(project);

        ContextHolder.setContext(project.getProjectId());

        MetadataSources metadata = new MetadataSources(
            new StandardServiceRegistryBuilder()
                .applySettings(getSchemaProperties())
                .build()
        );

        metadata.addAnnotatedClass(Branch.class);
        metadata.addAnnotatedClass(Commit.class);
        metadata.addAnnotatedClass(Node.class);
        metadata.addAnnotatedClass(NodeType.class);

        new SchemaExport()
            .setHaltOnError(false)
            .setFormat(true)
            .setDelimiter(";")
            .createOnly(EnumSet.of(TargetType.DATABASE),
                metadata.getMetadataBuilder().build());

        try (Connection conn = crudDataSources.getDataSource(project).getConnection()) {
           /* TODO rethink if project itself should be a node
            try (PreparedStatement ps = conn.prepareStatement(INITIAL_PROJECT)) {
                ps.setString(1, project.getProjectId());
                ps.setString(2, "test");
                ps.setString(3, "test");
                ps.setString(4, "test");
                ps.setInt(5, 1);
                ps.execute();
            }*/

            try (PreparedStatement ps = conn.prepareStatement(INITIAL_REF)) {
                ps.execute();
            }
        }
    }

    public void generateBranchSchemaFromModels() {
        MetadataSources metadata = new MetadataSources(
            new StandardServiceRegistryBuilder()
                .applySettings(getSchemaProperties())
                .build()
        );

        metadata.addAnnotatedClass(Node.class);

        new SchemaExport()
            .setHaltOnError(true)
            .setFormat(true)
            .setDelimiter(";")
            .createOnly(EnumSet.of(TargetType.DATABASE),
                metadata.getMetadataBuilder().build());
    }

    public void copyTablesFromParent(String target, String parent, String parentCommit) {
        if (parentCommit != null) {
            return;
        }

        final String targetNodeTable = String.format("nodes%s", target);
        StringBuilder parentNodeTable = new StringBuilder("nodes");

        if (parent != null && !parent.equalsIgnoreCase("master")) {
            parentNodeTable.append(String.format("%s", parent));
        }

        JdbcTemplate jdbcTemplate = new JdbcTemplate(
            crudDataSources.getDataSource(ContextHolder.getContext().getKey()));

        jdbcTemplate.execute("BEGIN");

        jdbcTemplate.execute(String.format(COPY_SQL, targetNodeTable, parentNodeTable));

        //reset db auto increment sequence for postgresql only
        if ("org.postgresql.Driver".equals(env.getProperty("spring.datasource.driver-class-name"))) {
            jdbcTemplate.execute(String.format(COPY_IDX, targetNodeTable, parentNodeTable));
        }

        jdbcTemplate.execute("COMMIT");
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
            "org.openmbee.sdvc.rdb.config.SuffixedPhysicalNamingStrategy");

        return properties;
    }
}

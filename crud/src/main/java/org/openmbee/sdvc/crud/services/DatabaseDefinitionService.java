package org.openmbee.sdvc.crud.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.openmbee.sdvc.core.config.PersistenceJPAConfig;
import org.openmbee.sdvc.data.domains.Project;
import org.openmbee.sdvc.crud.config.CurrentTenantIdentifierResolverImpl;
import org.openmbee.sdvc.crud.config.DataSourceBasedMultiTenantConnectionProviderImpl;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.repositories.edge.EdgeRowMapper;
import org.openmbee.sdvc.crud.repositories.node.NodeRowMapper;
import org.openmbee.sdvc.data.domains.Branch;
import org.openmbee.sdvc.data.domains.Commit;
import org.openmbee.sdvc.data.domains.Edge;
import org.openmbee.sdvc.data.domains.EdgeType;
import org.openmbee.sdvc.data.domains.Node;
import org.openmbee.sdvc.data.domains.NodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class DatabaseDefinitionService {

    private static final Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
    private static final String COPY_SQL = "INSERT INTO %s SELECT * FROM %s";
    private static final String COPY_IDX = "SELECT SETVAL('%s_id_seq', COALESCE((SELECT MAX(id) FROM %s), 1), true)";

    private static final String INITIAL_PROJECT = "INSERT INTO nodes (id, nodeid, indexid, initialcommit, lastcommit, nodetype, deleted) VALUES (0, ?, ?, ?, ?, ?, false)";
    private static final String INITIAL_REF = "INSERT INTO branches (id, branchid, branchname, tag, deleted, timestamp) VALUES (0, 'master', 'master', false, false, NOW());";

    private static final String GET_CHILDREN =
        "CREATE OR REPLACE FUNCTION get_children(integer, integer, text, integer)\n"
            + " RETURNS TABLE(id bigint)\n"
            + "AS $$\n"
            + "  begin\n"
            + "    return query\n"
            + "    execute '\n"
            + "    with recursive children(depth, nid, path, cycle, deleted) as (\n"
            + "      select 0 as depth, node.id, ARRAY[node.id], false, node.deleted from ' || format('nodes%s', $3) || '\n"
            + "        node where node.id = ' || $1 || ' union\n"
            + "      select (c.depth + 1) as depth, edge.child as nid, path || cast(edge.child as bigint) as path, edge.child = ANY(path) as cycle, node.deleted as deleted\n"
            + "        from ' || format('edges%s', $3) || ' edge, children c, ' || format('nodes%s', $3) || ' node where edge.parent = nid and node.id = edge.child and node.deleted = false and\n"
            + "        edge.edgeType = ' || $2 || ' and not cycle and depth < ' || $4 || '\n"
            + "      )\n"
            + "      select distinct nid from children;';\n"
            + "  end;\n"
            + "$$ LANGUAGE plpgsql;";

    private static final String GET_PARENTS =
        "CREATE OR REPLACE FUNCTION get_parents(integer, integer, text)\n"
            + " RETURNS TABLE(id bigint, height integer, root boolean)\n"
            + "AS $$\n"
            + "  begin\n"
            + "    return query\n"
            + "    execute '\n"
            + "    with recursive parents(height, nid, path, cycle) as (\n"
            + "    select 0, node.id, ARRAY[node.id], false from ' || format('nodes%s', $3) || ' node where node.id = ' || $1 || '\n"
            + "    union\n"
            + "      select (c.height + 1), edge.parent, path || cast(edge.parent as bigint),\n"
            + "        edge.parent = ANY(path) from ' || format('edges%s', $3) || '\n"
            + "        edge, parents c where edge.child = nid and edge.edgeType = ' || $2 || '\n"
            + "        and not cycle\n"
            + "      )\n"
            + "      select nid,height,(not exists (select true from edges where child = nid and edgetype = ' || $2 || '))\n"
            + "        from parents order by height desc;';\n"
            + "  end;\n"
            + "$$ LANGUAGE plpgsql;";

    private static final String GET_DOC_GROUPS =
        "CREATE OR REPLACE FUNCTION get_group_docs(integer, integer, text, integer, integer, integer)\n"
            + " RETURNS TABLE(id bigint)\n"
            + "AS $$\n"
            + "  begin\n"
            + "    return query\n"
            + "    execute '\n"
            + "    with recursive children(depth, nid, path, cycle, deleted, ntype) as (\n"
            + "      select 0 as depth, node.id, ARRAY[node.id], false, node.deleted, node.nodetype from ' || format('nodes%s', $3) || '\n"
            + "        node where node.id = ' || $1 || '  union\n"
            + "      select (c.depth + 1) as depth, edge.child as nid, path || cast(edge.child as bigint) as path, edge.child = ANY(path) as cycle, node.deleted as deleted, node.nodetype as ntype\n"
            + "        from ' || format('edges%s', $3) || ' edge, children c, ' || format('nodes%s', $3) || ' node where edge.parent = nid and node.id = edge.child and node.deleted = false and\n"
            + "        edge.edgeType = ' || $2 || ' and not cycle and depth < ' || $4 || ' and (node.nodetype <> '|| $5 ||' or nid = ' || $1 || ')\n"
            + "      )\n"
            + "      select distinct nid from children where ntype = ' || $6 || ';';\n"
            + "  end;\n"
            + "$$ LANGUAGE plpgsql;";

    private static final String GET_IMMEDIATE_PARENTS =
        "CREATE OR REPLACE FUNCTION get_immediate_parents(integer, integer, text)\n"
            + " RETURNS TABLE(nodeid text, indexid text)\n"
            + "AS $$\n"
            + "  begin\n"
            + "    return query\n"
            + "    execute '\n"
            + "    select nodeid, indexid from nodes' || $3 || ' where id in\n"
            + "      (select id from get_parents(' || $1 || ',' || $2 || ',''' || format('%s',$3) ||\n"
            + "      ''') where height = 1);';\n"
            + "  end;\n"
            + "$$ LANGUAGE plpgsql;";

    protected final Logger logger = LogManager.getLogger(getClass());
    private Map<String, DataSource> crudDataSources;
    private Environment env;

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    @Autowired
    public void setCrudDataSources(
        @Qualifier("crudDataSources") Map<String, DataSource> crudDataSources) {
        this.crudDataSources = crudDataSources;
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

            jdbcTemplate = new JdbcTemplate(crudDataSources.get(project.getProjectId()));
            jdbcTemplate.execute(GET_CHILDREN);
            jdbcTemplate.execute(GET_PARENTS);
            jdbcTemplate.execute(GET_IMMEDIATE_PARENTS);
            jdbcTemplate.execute(GET_DOC_GROUPS);

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

        MetadataSources metadata = new MetadataSources(
            new StandardServiceRegistryBuilder()
                .applySettings(getSchemaProperties())
                .build()
        );

        metadata.addAnnotatedClass(Branch.class);
        metadata.addAnnotatedClass(Commit.class);
        metadata.addAnnotatedClass(Edge.class);
        metadata.addAnnotatedClass(EdgeType.class);
        metadata.addAnnotatedClass(Node.class);
        metadata.addAnnotatedClass(NodeType.class);

        new SchemaExport()
            .setHaltOnError(false)
            .setFormat(true)
            .setDelimiter(";")
            .createOnly(EnumSet.of(TargetType.DATABASE),
                metadata.getMetadataBuilder().build());

        try (Connection conn = ds.getConnection()) {
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
        } catch (SQLException e) {

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

        jdbcTemplate.execute("BEGIN");

        jdbcTemplate.execute(String.format(COPY_SQL, targetNodeTable, parentNodeTable));
        jdbcTemplate.execute(String.format(COPY_SQL, targetEdgeTable, parentEdgeTable));
        //copyTables(jdbcTemplate, COPY_SQL, targetNodeTable, parentNodeTable.toString(), new NodeRowMapper());
        //copyTables(jdbcTemplate, COPY_SQL, targetEdgeTable, parentEdgeTable.toString(), new EdgeRowMapper());

        jdbcTemplate.execute(String.format(COPY_IDX, targetNodeTable, parentNodeTable));
        jdbcTemplate.execute(String.format(COPY_IDX, targetEdgeTable, parentEdgeTable));
        //copyTables(jdbcTemplate, COPY_IDX, targetNodeTable, parentNodeTable.toString(), new NodeRowMapper());
        //copyTables(jdbcTemplate, COPY_IDX, targetEdgeTable, parentEdgeTable.toString(), new EdgeRowMapper());

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
            "org.openmbee.sdvc.crud.config.SuffixedPhysicalNamingStrategy");

        return properties;
    }

    private void copyTables(JdbcTemplate jdbcTemplate, String sql, String target, String parent, RowMapper<?> rowMapper) {
        jdbcTemplate.query(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, target);
                ps.setString(2, parent);
            }
        }, rowMapper);
    }
}

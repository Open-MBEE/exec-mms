package org.openmbee.sdvc.crud.controllers.branches;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.persistence.Transient;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.openmbee.sdvc.crud.config.CurrentTenantIdentifierResolverImpl;
import org.openmbee.sdvc.crud.config.DataSourceBasedMultiTenantConnectionProviderImpl;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.domains.Edge;
import org.openmbee.sdvc.crud.domains.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class BranchesOperations implements Callable<Map<String, Object>>, Serializable {

    private static final long serialVersionUID = 561464450547556131L;

    private static final String refScript =
        "{\"script\": {\"inline\": \"if(ctx._source.containsKey(\\\"%1$s\\\")){ctx._source.%1$s.add(params.refId)} else {ctx._source.%1$s = [params.refId]}\", \"params\":{\"refId\":\"%2$s\"}}}";

    private static final String copySql = "INSERT INTO %s SELECT * FROM %s";

    protected final Logger logger = LogManager.getLogger(getClass());

    private transient Map<String, DataSource> crudDataSources;
    private transient Environment env;

    @Autowired
    public void setCrudDataSources(
        @Qualifier("crudDataSources") Map<String, DataSource> crudDataSources) {
        this.crudDataSources = crudDataSources;
    }

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    @Override public Map<String, Object> call() {
        return createBranch();
    }

    public Map<String, Object> createBranch() {
        logger.error(
            "Current Context is: {} on ref {}", DbContextHolder.getContext().getProjectId(),
            DbContextHolder.getContext().getBranchId());
        createTablesForBranch();
        return new HashMap<>();
    }

    public void copyTablesFromParent(String target, String parent) {
        final String targetNodeTable = String.format("nodes_%s", target);
        final String targetEdgeTable = String.format("edges_%s", target);
        StringBuilder parentNodeTable = new StringBuilder("nodes");
        StringBuilder parentEdgeTable = new StringBuilder("edges");

        if (parent != null && !parent.equalsIgnoreCase("master")) {
            parentNodeTable.append(String.format("_%s", parent));
            parentEdgeTable.append(String.format("_%s", parent));
        }

        JdbcTemplate jdbcTemplate = new JdbcTemplate(
            crudDataSources.get(DbContextHolder.getContext().getKey()));
        jdbcTemplate.execute(String.format(copySql, targetNodeTable, parentNodeTable));
        jdbcTemplate.execute(String.format(copySql, targetEdgeTable, parentEdgeTable));
    }

    public void createTablesForBranch() {
        MetadataSources metadata = new MetadataSources(
            new StandardServiceRegistryBuilder()
                .applySettings(getSchemaProperties())
                .build()
        );

        metadata.addAnnotatedClass(Edge.class);
        metadata.addAnnotatedClass(Node.class);

        new SchemaExport()
            .setHaltOnError(false)
            .setFormat(true)
            .setDelimiter(";")
            .createOnly(EnumSet.of(TargetType.DATABASE),
                metadata.getMetadataBuilder().build());
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

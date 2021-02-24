package org.openmbee.mms.rdb.config;


import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.openmbee.mms.core.config.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuffixedPhysicalNamingStrategy implements PhysicalNamingStrategy {

    public static final PhysicalNamingStrategyStandardImpl INSTANCE = new PhysicalNamingStrategyStandardImpl();
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return name;
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return name;
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return name;
    }

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        String refId = ContextHolder.getContext().getBranchId();
        refId = DatabaseDefinitionService.getNodeTableName(refId);
        return new Identifier(compoundKey(name.getText(), refId), name.isQuoted());
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        return new Identifier(name.getText(), name.isQuoted());
    }

    private String compoundKey(String name, String refId) {
        return name + refId;
    }
}

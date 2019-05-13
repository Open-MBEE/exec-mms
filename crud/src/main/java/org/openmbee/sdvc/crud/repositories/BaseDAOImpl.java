package org.openmbee.sdvc.crud.repositories;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Map;
import javax.sql.DataSource;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

public abstract class BaseDAOImpl {

    private Map<String, DataSource> crudDataSources;
    public PlatformTransactionManager transactionManager;

    @Autowired
    public void setCrudDataSources(
        @Qualifier("crudDataSources") Map<String, DataSource> crudDataSources) {
        this.crudDataSources = crudDataSources;
    }

    @Autowired
    public void setTransactionManager(
        @Qualifier("crudTransactionManager") PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public PlatformTransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    public JdbcTemplate getConn() {
        return new JdbcTemplate(crudDataSources.get(DbContextHolder.getContext().getKey()));
    }

    public String getSuffix() {
        return DbContextHolder.getContext().getDbTableSuffix();
    }
}

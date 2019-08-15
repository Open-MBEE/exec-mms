package org.openmbee.sdvc.rdb.repositories;

import java.util.Map;
import javax.sql.DataSource;
import org.openmbee.sdvc.core.config.ContextHolder;
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
        PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public PlatformTransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    public JdbcTemplate getConn() {
        return new JdbcTemplate(crudDataSources.get(ContextHolder.getContext().getKey()));
    }

    public String getSuffix() {
        return ContextHolder.getContext().getDbTableSuffix();
    }
}

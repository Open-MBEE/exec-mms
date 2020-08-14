package org.openmbee.sdvc.rdb.repositories;

import org.openmbee.sdvc.core.config.ContextHolder;
import org.openmbee.sdvc.core.config.ContextObject;
import org.openmbee.sdvc.rdb.datasources.CrudDataSources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

public abstract class BaseDAOImpl {

    private CrudDataSources crudDataSources;
    public PlatformTransactionManager transactionManager;

    @Autowired
    public void setCrudDataSources(CrudDataSources crudDataSources) {
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
        return new JdbcTemplate(crudDataSources.getDataSource(ContextHolder.getContext().getKey()));
    }

    public String getSuffix() {
        String refId = ContextHolder.getContext().getBranchId();
        return refId.equals(ContextObject.MASTER_BRANCH) ? "" : refId.toLowerCase();
    }
}

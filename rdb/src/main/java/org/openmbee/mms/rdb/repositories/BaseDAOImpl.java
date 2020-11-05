package org.openmbee.mms.rdb.repositories;

import org.openmbee.mms.core.config.ContextHolder;
import org.openmbee.mms.core.config.ContextObject;
import org.openmbee.mms.rdb.datasources.CrudDataSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

public abstract class BaseDAOImpl {

    private CrudDataSources crudDataSources;
    public PlatformTransactionManager transactionManager;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

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

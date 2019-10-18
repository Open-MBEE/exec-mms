package org.openmbee.sdvc.core.dao;

import org.springframework.transaction.PlatformTransactionManager;

public interface BaseDAO {
    public PlatformTransactionManager getTransactionManager();
}

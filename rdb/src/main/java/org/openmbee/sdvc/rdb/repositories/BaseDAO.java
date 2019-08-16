package org.openmbee.sdvc.rdb.repositories;

import org.springframework.transaction.PlatformTransactionManager;

public interface BaseDAO {
    public PlatformTransactionManager getTransactionManager();
}

package org.openmbee.sdvc.crud.repositories;

import org.springframework.transaction.PlatformTransactionManager;

public interface BaseDAO {
    public PlatformTransactionManager getTransactionManager();
}

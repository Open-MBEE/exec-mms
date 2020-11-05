package org.openmbee.mms.core.dao;

import org.springframework.transaction.PlatformTransactionManager;

public interface BaseDAO {
    PlatformTransactionManager getTransactionManager();
}

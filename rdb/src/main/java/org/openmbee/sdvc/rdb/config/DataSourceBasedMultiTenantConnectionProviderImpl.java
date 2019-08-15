package org.openmbee.sdvc.rdb.config;

import java.util.Map;
import javax.sql.DataSource;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.openmbee.sdvc.core.config.ContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DataSourceBasedMultiTenantConnectionProviderImpl extends
    AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private static final long serialVersionUID = 1L;

    private Map<String, DataSource> crudDataSources;

    @Autowired
    public void setCrudDataSources(
        @Qualifier("crudDataSources") Map<String, DataSource> crudDataSources) {
        this.crudDataSources = crudDataSources;
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return this.crudDataSources.get(ContextHolder.getContext().getKey());
    }

    @Override
    protected DataSource selectDataSource(String tenantId) {
        return this.crudDataSources.get(tenantId);
    }
}
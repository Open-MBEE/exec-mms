package org.openmbee.sdvc.rdb.config;

import org.openmbee.sdvc.rdb.datasources.CrudDataSources;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingJDBCConfig {

    @Bean(name = "crudDataSources")
    public CrudDataSources crudDataSources() {
        return new CrudDataSources();
    }
}

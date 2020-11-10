package org.openmbee.mms.rdb.config;

import org.openmbee.mms.rdb.datasources.CrudDataSources;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingJDBCConfig {

    @Bean
    public CrudDataSources crudDataSources() {
        return new CrudDataSources();
    }
}

package org.openmbee.mms.permissions.config;

import org.openmbee.mms.core.delegation.PermissionsDelegateFactory;
import org.openmbee.mms.permissions.delegation.DefaultPermissionsDelegateFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class DefaultPermissionsDelegateConfig {

    @Bean
    @Order(0)
    public PermissionsDelegateFactory permissionsDelegateFactory(){
        return new DefaultPermissionsDelegateFactory();
    }

}

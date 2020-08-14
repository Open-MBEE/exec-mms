package org.openmbee.sdvc.permissions.config;

import org.openmbee.sdvc.core.delegation.PermissionsDelegateFactory;
import org.openmbee.sdvc.permissions.delegation.DefaultPermissionsDelegateFactory;
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

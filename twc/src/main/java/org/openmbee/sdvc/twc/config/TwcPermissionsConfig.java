package org.openmbee.sdvc.twc.config;

import org.openmbee.sdvc.core.delegation.PermissionsDelegateFactory;
import org.openmbee.sdvc.twc.permissions.TwcPermissionsDelegateFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class TwcPermissionsConfig {

    @Bean
    @Order(-1)
    public PermissionsDelegateFactory getPermissionsDelegateFactory() {
        return new TwcPermissionsDelegateFactory();
    }
}

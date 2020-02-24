package org.openmbee.sdvc.core.config;

import org.openmbee.sdvc.core.utils.RestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Rest {

    @Bean
    public RestUtils getRestUtils() {
        return new RestUtils();
    }

}

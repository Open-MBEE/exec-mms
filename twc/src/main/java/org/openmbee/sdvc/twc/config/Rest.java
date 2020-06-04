package org.openmbee.sdvc.twc.config;

import org.openmbee.sdvc.twc.utilities.RestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Rest {

    @Bean
    public RestUtils getRestUtils() {
        return new RestUtils();
    }

}

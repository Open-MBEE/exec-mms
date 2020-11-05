package org.openmbee.mms.twc.config;

import org.openmbee.mms.twc.utilities.RestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Rest {

    @Bean
    public RestUtils getRestUtils() {
        return new RestUtils();
    }

}

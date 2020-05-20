package org.openmbee.sdvc.core.config;

import org.openmbee.sdvc.core.utils.RequestUtils;
import org.openmbee.sdvc.core.utils.RestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Utils {

    @Bean
    public RestUtils getRestUtils() {
        return new RestUtils();
    }

    @Bean
    public RequestUtils getRequestUtils() {
        return new RequestUtils();
    }
}

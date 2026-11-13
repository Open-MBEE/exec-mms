package org.openmbee.mms.example.config;

import java.util.Map;
import org.springdoc.core.customizers.SpringDocCustomizers;
import org.springdoc.core.discoverer.SpringDocParameterNameDiscoverer;
import org.springdoc.core.extractor.MethodParameterPojoExtractor;
import org.springdoc.core.service.GenericParameterService;
import org.springdoc.core.service.RequestBodyService;
import org.springdoc.webmvc.core.service.RequestService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.MethodParameter;

@Configuration
public class OpenAPIConfig {

    @Bean
    @Primary
    SpringDocRequestBuilder myRequestBuilder(
        GenericParameterService parameterBuilder, RequestBodyService requestBodyBuilder,
        SpringDocCustomizers customizers,
        SpringDocParameterNameDiscoverer localSpringDocParameterNameDiscoverer,
        MethodParameterPojoExtractor methodParameterPojoExtractor) {
        return new SpringDocRequestBuilder(parameterBuilder, requestBodyBuilder, customizers,
            localSpringDocParameterNameDiscoverer, methodParameterPojoExtractor);
    }

    //ignore all injected Map types in controller methods
    public class SpringDocRequestBuilder extends RequestService {

        public SpringDocRequestBuilder(GenericParameterService parameterBuilder, RequestBodyService requestBodyBuilder,
            SpringDocCustomizers customizers,
            SpringDocParameterNameDiscoverer localSpringDocParameterNameDiscoverer,
            MethodParameterPojoExtractor methodParameterPojoExtractor) {
            super(parameterBuilder, requestBodyBuilder, customizers,
                localSpringDocParameterNameDiscoverer, methodParameterPojoExtractor);
        }

        @Override
        public boolean isParamToIgnore(MethodParameter parameter) {
            if (Map.class.isAssignableFrom(parameter.getParameterType()))
                return true;
            return super.isParamToIgnore(parameter);
        }
    }
}

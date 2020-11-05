package org.openmbee.mms.example.config;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springdoc.core.GenericParameterBuilder;
import org.springdoc.core.OperationBuilder;
import org.springdoc.core.RequestBodyBuilder;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springdoc.webmvc.core.RequestBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;

@Configuration
public class OpenAPIConfig {

    @Bean
    @Primary
    SpringDocRequestBuilder myRequestBuilder(
        GenericParameterBuilder parameterBuilder, RequestBodyBuilder requestBodyBuilder,
        OperationBuilder operationBuilder, Optional<List<OperationCustomizer>> customizers,
        Optional<List<ParameterCustomizer>> parameterCustomizers,
        LocalVariableTableParameterNameDiscoverer localSpringDocParameterNameDiscoverer) {
        return new SpringDocRequestBuilder(parameterBuilder, requestBodyBuilder, operationBuilder, customizers,
            parameterCustomizers, localSpringDocParameterNameDiscoverer);
    }

    //ignore all injected Map types in controller methods
    public class SpringDocRequestBuilder extends RequestBuilder {

        public SpringDocRequestBuilder(GenericParameterBuilder parameterBuilder, RequestBodyBuilder requestBodyBuilder,
            OperationBuilder operationBuilder, Optional<List<OperationCustomizer>> customizers,
            Optional<List<ParameterCustomizer>> parameterCustomizers,
            LocalVariableTableParameterNameDiscoverer localSpringDocParameterNameDiscoverer) {
            super(parameterBuilder, requestBodyBuilder, operationBuilder, customizers,
                parameterCustomizers, localSpringDocParameterNameDiscoverer);
        }

        @Override
        protected boolean isParamToIgnore(MethodParameter parameter) {
            if (Map.class.isAssignableFrom(parameter.getParameterType()))
                return true;
            return super.isParamToIgnore(parameter);
        }
    }
}

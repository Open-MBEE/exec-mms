package org.openmbee.sdvc.example;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import java.util.Arrays;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = "org.openmbee")
@EnableSwagger2
@OpenAPIDefinition(
    info = @Info(
        title = "MMS Example API",
        version = "0.0.1",
        description = "Documentation for MMS API",
        license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.txt")
    ),
    servers = {
    },
    security = {@SecurityRequirement(name = "basicAuth"), @SecurityRequirement(name = "bearerToken")}
)
/*@SecurityScheme(
    name = "basicAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "basic"
)*/ //can't get multiple security scheme to show
@SecurityScheme(
    name = "bearerToken",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class ExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .ignoredParameterTypes(Authentication.class, Map.class)
            .apiInfo(apiInfo()).select()
            .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
            .paths(PathSelectors.any()).build()
            .securitySchemes(Arrays.asList(
                new BasicAuth("basicAuth"),
                new ApiKey("bearerToken", "Authorization", "header")))
            .securityContexts(Arrays.asList(
                SecurityContext.builder().securityReferences(Arrays.asList(
                    new SecurityReference("basicAuth", new AuthorizationScope[0]),
                    new SecurityReference("bearerToken", new AuthorizationScope[0])
                ))
                .forPaths(PathSelectors.any()).build()
            ));
    }
    
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("MMS Example API")
            .description("Documentation for MMS Example API").termsOfServiceUrl("")
            .contact(new Contact("OpenMBEE", "http://www.openmbee.org",
                ""))
            .license("Apache License Version 2.0").licenseUrl("").version("0.0.1").build();
    }
}

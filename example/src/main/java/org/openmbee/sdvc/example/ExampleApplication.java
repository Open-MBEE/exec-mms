package org.openmbee.sdvc.example;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.openmbee.sdvc.artifacts.storage.ArtifactStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SpringBootApplication(scanBasePackages = "org.openmbee")
@OpenAPIDefinition(
    info = @Info(
        title = "MMS Example API",
        version = "0.0.1",
        description = "Documentation for MMS API",
        license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.txt")
    ),
    security = {@SecurityRequirement(name = "basicAuth"), @SecurityRequirement(name = "bearerToken")}
)
/*@SecuritySchemes(value = {
@SecurityScheme(
    name = "basicAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "basic"
),*///can't get multiple security scheme to show
@SecurityScheme(
    name = "bearerToken",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)//})
public class ExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter
            = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        filter.setAfterMessagePrefix("REQUEST DATA : ");
        return filter;
    }

    @Component
    @WebFilter("/*")
    public class ResponseFilter implements Filter {
        private final Logger LOGGER = LoggerFactory.getLogger(ResponseFilter.class);
        @Override
        public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
            long time = System.currentTimeMillis();
            HttpServletResponseCopier responseCopier = new HttpServletResponseCopier((HttpServletResponse) resp);

            try {
                chain.doFilter(req, responseCopier);
                responseCopier.flushBuffer();

            } finally {
                byte[] copy = responseCopier.getCopy();
                time = System.currentTimeMillis() - time;
                LOGGER.info("{}: {} ms ", ((HttpServletRequest) req).getRequestURI(),  time);
                LOGGER.info(new String(copy, resp.getCharacterEncoding()));
            }
        }
    }

    //TODO: remove. This is just for testing until the real open source implementation is done.
    @Bean
    public ArtifactStorage getArtifactStorage() {
        return new InMemoryArtifactStorage();
    }
}

package sample;

import org.briskidentity.bearerauth.context.PropertiesAuthorizationContextResolver;
import org.briskidentity.bearerauth.servlet.ServletBearerAuthenticationFilter;
import org.briskidentity.bearerauth.spring.RequiresScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.Collections;

@SpringBootApplication
@RestController
public class SpringMvcTestApplication {

    private static final Logger logger = LoggerFactory.getLogger(SpringMvcTestApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringMvcTestApplication.class, args);
    }

    @GetMapping(path = "/resource")
    @RequiresScope("scope:read")
    public String greet(WebRequest webRequest) {
        logger.info("Principal: {}", webRequest.getUserPrincipal());
        return "Hello World!";
    }

    @Bean
    public FilterRegistrationBean<ServletBearerAuthenticationFilter> bearerAuthenticationFilter() throws IOException {
        FilterRegistrationBean<ServletBearerAuthenticationFilter> servletBearerAuthenticationFilter =
                new FilterRegistrationBean<>(new ServletBearerAuthenticationFilter(
                        new PropertiesAuthorizationContextResolver()));
        servletBearerAuthenticationFilter.setUrlPatterns(Collections.singleton("/resource/*"));
        return servletBearerAuthenticationFilter;
    }

}

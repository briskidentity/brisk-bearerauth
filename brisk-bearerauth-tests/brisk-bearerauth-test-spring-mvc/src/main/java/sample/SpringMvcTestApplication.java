package sample;

import org.briskidentity.bearerauth.BearerAuthenticationHandler;
import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.context.PropertiesAuthorizationContextResolver;
import org.briskidentity.bearerauth.servlet.ServletBearerAuthenticationFilter;
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

    public static void main(String[] args) {
        SpringApplication.run(SpringMvcTestApplication.class, args);
    }

    @GetMapping(path = "/resource")
    public String greet(WebRequest webRequest) {
        AuthorizationContext authorizationContext = (AuthorizationContext) webRequest.getUserPrincipal();
        System.out.println("authorizationContext{scope=" + String.join(",", authorizationContext.getScopeValues())
                + ",expiry=" + authorizationContext.getExpiry() + "}");
        return "Hello World!";
    }

    @Bean
    public FilterRegistrationBean<ServletBearerAuthenticationFilter> bearerAuthenticationFilter() throws IOException {
        BearerAuthenticationHandler bearerAuthenticationHandler = BearerAuthenticationHandler.builder(
                new PropertiesAuthorizationContextResolver()).build();
        FilterRegistrationBean<ServletBearerAuthenticationFilter> servletBearerAuthenticationFilter =
                new FilterRegistrationBean<>(new ServletBearerAuthenticationFilter(bearerAuthenticationHandler));
        servletBearerAuthenticationFilter.setUrlPatterns(Collections.singleton("/resource/*"));
        return servletBearerAuthenticationFilter;
    }

}

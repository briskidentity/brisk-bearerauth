package sample;

import org.briskidentity.bearerauth.BearerAuthenticationHandler;
import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.context.AuthorizationContextResolver;
import org.briskidentity.bearerauth.context.MapAuthorizationContextResolver;
import org.briskidentity.bearerauth.context.validation.AuthorizationContextValidator;
import org.briskidentity.bearerauth.context.validation.DefaultAuthorizationContextValidator;
import org.briskidentity.bearerauth.context.validation.ScopeMapping;
import org.briskidentity.bearerauth.servlet.ServletBearerAuthenticationFilter;
import org.briskidentity.bearerauth.token.BearerToken;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public FilterRegistrationBean<ServletBearerAuthenticationFilter> bearerAuthenticationFilter() {
        Map<BearerToken, AuthorizationContext> authorizationContexts = new HashMap<>();
        authorizationContexts.put(new BearerToken("valid_token"),
                new AuthorizationContext(Collections.singleton("scope:read"), Instant.MAX, Collections.emptyMap()));
        authorizationContexts.put(new BearerToken("token_expired"),
                new AuthorizationContext(Collections.singleton("scope:read"), Instant.MIN, Collections.emptyMap()));
        authorizationContexts.put(new BearerToken("insufficient_scope"),
                new AuthorizationContext(Collections.emptySet(), Instant.MAX, Collections.emptyMap()));
        AuthorizationContextResolver authorizationContextResolver =
                new MapAuthorizationContextResolver(authorizationContexts);
        List<ScopeMapping> scopeMappings = new ArrayList<>();
        scopeMappings.add(new ScopeMapping("/resource", "GET", Collections.singleton("scope:read")));
        AuthorizationContextValidator authorizationContextValidator = new DefaultAuthorizationContextValidator(
                scopeMappings);
        BearerAuthenticationHandler bearerAuthenticationHandler = BearerAuthenticationHandler.builder(
                authorizationContextResolver).authorizationContextValidator(authorizationContextValidator).build();
        FilterRegistrationBean<ServletBearerAuthenticationFilter> servletBearerAuthenticationFilter =
                new FilterRegistrationBean<>(new ServletBearerAuthenticationFilter(bearerAuthenticationHandler));
        servletBearerAuthenticationFilter.setUrlPatterns(Collections.singleton("/resource/*"));
        return servletBearerAuthenticationFilter;
    }

}

package sample;

import org.briskidentity.bearerauth.AuthorizationContext;
import org.briskidentity.bearerauth.BearerAuthenticationHandler;
import org.briskidentity.bearerauth.BearerToken;
import org.briskidentity.bearerauth.MapAuthorizationContextResolver;
import org.briskidentity.bearerauth.servlet.ServletBearerAuthenticationFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class SpringMvcTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringMvcTestApplication.class, args);
    }

    @GetMapping(path = "/resource")
    public String greet(WebRequest webRequest) {
        AuthorizationContext authorizationContext = (AuthorizationContext) webRequest.getAttribute(
                BearerAuthenticationHandler.AUTHORIZATION_CONTEXT_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        System.out.println("authorizationContext{scope=" + String.join(",", authorizationContext.getScope())
                + ",expiry=" + authorizationContext.getExpiry() + "}");
        return "Hello World!";
    }

    @Bean
    public FilterRegistrationBean<ServletBearerAuthenticationFilter> bearerAuthenticationFilter() {
        Map<BearerToken, AuthorizationContext> authorizationContexts = new HashMap<>();
        authorizationContexts.put(new BearerToken("valid"),
                new AuthorizationContext(Collections.emptySet(), Instant.MAX, Collections.emptyMap()));
        authorizationContexts.put(new BearerToken("expired"),
                new AuthorizationContext(Collections.emptySet(), Instant.MIN, Collections.emptyMap()));
        BearerAuthenticationHandler bearerAuthenticationHandler = BearerAuthenticationHandler.builder(
                new MapAuthorizationContextResolver(authorizationContexts)).build();
        FilterRegistrationBean<ServletBearerAuthenticationFilter> servletBearerAuthenticationFilter =
                new FilterRegistrationBean<>(new ServletBearerAuthenticationFilter(bearerAuthenticationHandler));
        servletBearerAuthenticationFilter.setUrlPatterns(Collections.singleton("/resource"));
        return servletBearerAuthenticationFilter;
    }

}

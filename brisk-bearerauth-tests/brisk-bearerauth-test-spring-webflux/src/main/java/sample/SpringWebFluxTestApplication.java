package sample;

import org.briskidentity.bearerauth.BearerAuthenticationHandler;
import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.context.AuthorizationContextResolver;
import org.briskidentity.bearerauth.context.MapAuthorizationContextResolver;
import org.briskidentity.bearerauth.context.validation.AuthorizationContextValidator;
import org.briskidentity.bearerauth.context.validation.DefaultAuthorizationContextValidator;
import org.briskidentity.bearerauth.context.validation.ScopeMapping;
import org.briskidentity.bearerauth.spring.webflux.WebFluxBearerAuthenticationFilter;
import org.briskidentity.bearerauth.token.BearerToken;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
public class SpringWebFluxTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringWebFluxTestApplication.class, args);
    }

    @GetMapping(path = "/resource")
    public String greet(ServerWebExchange exchange) {
        exchange.getPrincipal().subscribe(principal -> {
            AuthorizationContext authorizationContext = (AuthorizationContext) principal;
            System.out.println("authorizationContext{scope=" + String.join(",", authorizationContext.getScopeValues())
                    + ",expiry=" + authorizationContext.getExpiry() + "}");
        });
        return "Hello World!";
    }

    @Bean
    public WebFluxBearerAuthenticationFilter bearerAuthenticationFilter() {
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
        return new WebFluxBearerAuthenticationFilter(bearerAuthenticationHandler);
    }

}

package sample;

import org.briskidentity.bearerauth.BearerAuthenticationHandler;
import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.context.MapAuthorizationContextResolver;
import org.briskidentity.bearerauth.spring.webflux.WebFluxBearerAuthenticationFilter;
import org.briskidentity.bearerauth.token.BearerToken;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class SpringWebFluxTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringWebFluxTestApplication.class, args);
    }

    @GetMapping(path = "/resource")
    public String greet(ServerWebExchange exchange) {
        AuthorizationContext authorizationContext = exchange.getAttribute(
                BearerAuthenticationHandler.AUTHORIZATION_CONTEXT_ATTRIBUTE);
        System.out.println("authorizationContext{scope=" + String.join(",", authorizationContext.getScopeValues())
                + ",expiry=" + authorizationContext.getExpiry() + "}");
        return "Hello World!";
    }

    @Bean
    public WebFluxBearerAuthenticationFilter bearerAuthenticationFilter() {
        Map<BearerToken, AuthorizationContext> authorizationContexts = new HashMap<>();
        authorizationContexts.put(new BearerToken("valid"),
                new AuthorizationContext(Collections.emptySet(), Instant.MAX, Collections.emptyMap()));
        authorizationContexts.put(new BearerToken("expired"),
                new AuthorizationContext(Collections.emptySet(), Instant.MIN, Collections.emptyMap()));
        BearerAuthenticationHandler bearerAuthenticationHandler = BearerAuthenticationHandler.builder(
                new MapAuthorizationContextResolver(authorizationContexts)).build();
        return new WebFluxBearerAuthenticationFilter(bearerAuthenticationHandler);
    }

}

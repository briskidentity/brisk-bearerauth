package sample;

import io.github.vpavic.bearerauth.AuthorizationContext;
import io.github.vpavic.bearerauth.AuthorizationContextResolver;
import io.github.vpavic.bearerauth.BearerAuthenticationHandler;
import io.github.vpavic.bearerauth.HttpExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Component
public class WebFluxBearerAuthenticationFilter implements WebFilter {

    private final BearerAuthenticationHandler bearerAuthenticationHandler;

    public WebFluxBearerAuthenticationFilter() {
        this.bearerAuthenticationHandler = BearerAuthenticationHandler.builder(
                bearerToken -> authorizationContextResolver().apply(bearerToken)).build();
    }

    private AuthorizationContextResolver authorizationContextResolver() {
        return bearerToken -> {
            switch (bearerToken.toString()) {
                case "valid":
                    return CompletableFuture.completedFuture(
                            new AuthorizationContext(Collections.emptySet(), Instant.MAX, Collections.emptyMap()));
                case "expired":
                    return CompletableFuture.completedFuture(
                            new AuthorizationContext(Collections.emptySet(), Instant.MIN, Collections.emptyMap()));
                default:
                    return CompletableFuture.completedFuture(null);
            }
        };
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.fromCompletionStage(this.bearerAuthenticationHandler.handle(new WebFluxHttpExchange(exchange)))
                .then(chain.filter(exchange));
    }

    private static class WebFluxHttpExchange implements HttpExchange {

        private final ServerWebExchange serverWebExchange;

        private WebFluxHttpExchange(ServerWebExchange serverWebExchange) {
            this.serverWebExchange = serverWebExchange;
        }

        @Override
        public String getRequestHeader(String headerName) {
            return this.serverWebExchange.getRequest().getHeaders().getFirst(headerName);
        }

        @Override
        public void setAttribute(String attributeName, Object attributeValue) {
            serverWebExchange.getAttributes().put(attributeName, attributeValue);
        }

    }

}

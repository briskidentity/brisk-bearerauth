package io.github.vpavic.bearerauth.spring.webflux;

import io.github.vpavic.bearerauth.BearerAuthenticationHandler;
import io.github.vpavic.bearerauth.BearerTokenException;
import io.github.vpavic.bearerauth.HttpExchange;
import io.github.vpavic.bearerauth.WwwAuthenticateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class WebFluxBearerAuthenticationFilter implements WebFilter {

    private final BearerAuthenticationHandler bearerAuthenticationHandler;

    public WebFluxBearerAuthenticationFilter(BearerAuthenticationHandler bearerAuthenticationHandler) {
        Objects.requireNonNull(bearerAuthenticationHandler, "bearerAuthenticationHandler must not be null");
        this.bearerAuthenticationHandler = bearerAuthenticationHandler;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.fromCompletionStage(this.bearerAuthenticationHandler.handle(new WebFluxHttpExchange(exchange)))
                .then(chain.filter(exchange))
                .onErrorResume(BearerTokenException.class, ex -> {
                    String wwwAuthenticate = WwwAuthenticateBuilder.from(ex).build();
                    ServerHttpResponse response = exchange.getResponse();
                    response.getHeaders().set(HttpHeaders.WWW_AUTHENTICATE, wwwAuthenticate);
                    response.setStatusCode(HttpStatus.resolve(ex.getStatus()));
                    return Mono.empty();
                });
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

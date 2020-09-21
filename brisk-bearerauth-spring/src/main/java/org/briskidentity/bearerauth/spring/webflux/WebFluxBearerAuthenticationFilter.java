package org.briskidentity.bearerauth.spring.webflux;

import org.briskidentity.bearerauth.BearerAuthenticationHandler;
import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.http.HttpExchange;
import org.briskidentity.bearerauth.http.WwwAuthenticateBuilder;
import org.briskidentity.bearerauth.token.error.BearerTokenException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.security.Principal;
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
                .then(chain.filter(new AuthorizedExchange(exchange)))
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
        public String getRequestMethod() {
            return this.serverWebExchange.getRequest().getMethod().name();
        }

        @Override
        public String getRequestPath() {
            return this.serverWebExchange.getRequest().getURI().getPath();
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

    private static class AuthorizedExchange extends ServerWebExchangeDecorator {

        private AuthorizedExchange(ServerWebExchange delegate) {
            super(delegate);
        }

        @Override
        public <T extends Principal> Mono<T> getPrincipal() {
            @SuppressWarnings("unchecked")
            Mono<T> principal = (Mono<T>) Mono.justOrEmpty(getAuthorizationContext());
            return principal;
        }

        private AuthorizationContext getAuthorizationContext() {
            Object authorizationContext = getAttribute(BearerAuthenticationHandler.AUTHORIZATION_CONTEXT_ATTRIBUTE);
            if ((authorizationContext instanceof AuthorizationContext)) {
                return (AuthorizationContext) authorizationContext;
            }
            return null;
        }

    }

}

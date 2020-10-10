package org.briskidentity.bearerauth.spring.webflux;

import org.briskidentity.bearerauth.BearerAuthenticationHandler;
import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.http.ProtectedResourceRequest;
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
        return Mono.fromCompletionStage(this.bearerAuthenticationHandler.handle(new WebFluxProtectedResourceRequest(exchange)))
                .flatMap(authorizationContext -> chain.filter(new AuthorizedExchange(exchange, authorizationContext)))
                .onErrorResume(BearerTokenException.class, ex -> {
                    String wwwAuthenticate = WwwAuthenticateBuilder.from(ex).build();
                    ServerHttpResponse response = exchange.getResponse();
                    response.getHeaders().set(HttpHeaders.WWW_AUTHENTICATE, wwwAuthenticate);
                    response.setStatusCode(HttpStatus.resolve(ex.getStatus()));
                    return Mono.empty();
                });
    }

    private static class WebFluxProtectedResourceRequest implements ProtectedResourceRequest {

        private final ServerWebExchange serverWebExchange;

        private WebFluxProtectedResourceRequest(ServerWebExchange serverWebExchange) {
            this.serverWebExchange = serverWebExchange;
        }

        @Override
        public String getAuthorizationHeader() {
            return this.serverWebExchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getNativeRequest() {
            return (T) this.serverWebExchange;
        }

    }

    private static class AuthorizedExchange extends ServerWebExchangeDecorator {

        private final AuthorizationContext authorizationContext;

        private AuthorizedExchange(ServerWebExchange delegate, AuthorizationContext authorizationContext) {
            super(delegate);
            this.authorizationContext = authorizationContext;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends Principal> Mono<T> getPrincipal() {
            return (Mono<T>) Mono.just(this.authorizationContext);
        }

    }

}

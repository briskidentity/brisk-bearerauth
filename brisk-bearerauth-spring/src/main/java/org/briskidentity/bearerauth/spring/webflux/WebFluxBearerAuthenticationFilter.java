/*
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.briskidentity.bearerauth.spring.webflux;

import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.context.AuthorizationContextResolver;
import org.briskidentity.bearerauth.http.ProtectedResourceRequest;
import org.briskidentity.bearerauth.http.WwwAuthenticateBuilder;
import org.briskidentity.bearerauth.token.BearerTokenExtractor;
import org.briskidentity.bearerauth.token.error.BearerTokenError;
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

    private final BearerTokenExtractor bearerTokenExtractor;

    private final AuthorizationContextResolver authorizationContextResolver;

    public WebFluxBearerAuthenticationFilter(BearerTokenExtractor bearerTokenExtractor,
            AuthorizationContextResolver authorizationContextResolver) {
        Objects.requireNonNull(bearerTokenExtractor, "bearerTokenExtractor must not be null");
        Objects.requireNonNull(authorizationContextResolver, "authorizationContextResolver must not be null");
        this.bearerTokenExtractor = bearerTokenExtractor;
        this.authorizationContextResolver = authorizationContextResolver;
    }

    public WebFluxBearerAuthenticationFilter(AuthorizationContextResolver authorizationContextResolver) {
        this(BearerTokenExtractor.authorizationHeader(), authorizationContextResolver);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.defer(() -> Mono.just(this.bearerTokenExtractor.extract(new WebFluxProtectedResourceRequest(exchange))))
                .flatMap(bearerToken -> Mono.fromCompletionStage(this.authorizationContextResolver.resolve(bearerToken)))
                .flatMap(authorizationContext -> chain.filter(new AuthorizedExchange(exchange, authorizationContext)))
                .onErrorResume(BearerTokenException.class, ex -> handleBearerTokenError(ex.getError(), exchange.getResponse()));
    }

    private Mono<Void> handleBearerTokenError(BearerTokenError bearerTokenError, ServerHttpResponse response) {
        String wwwAuthenticate = WwwAuthenticateBuilder.from(bearerTokenError).build();
        response.getHeaders().set(HttpHeaders.WWW_AUTHENTICATE, wwwAuthenticate);
        response.setStatusCode(HttpStatus.resolve(bearerTokenError.getHttpStatus()));
        return Mono.empty();
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

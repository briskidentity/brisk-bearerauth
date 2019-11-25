package sample;

import io.github.vpavic.bearerauth.AuthorizationContext;
import io.github.vpavic.bearerauth.AuthorizationContextResolver;
import io.github.vpavic.bearerauth.BearerAuthenticationHandler;
import io.github.vpavic.bearerauth.HttpExchange;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.OncePerRequestHttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.reactivex.Completable;
import org.reactivestreams.Publisher;

import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@Filter("/**")
public class MicronautBearerAuthenticationFilter extends OncePerRequestHttpServerFilter {

    private final BearerAuthenticationHandler bearerAuthenticationHandler;

    public MicronautBearerAuthenticationFilter() {
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
    public Publisher<MutableHttpResponse<?>> doFilterOnce(HttpRequest<?> request, ServerFilterChain chain) {
        return Completable.fromFuture(
                this.bearerAuthenticationHandler.handle(new MicronautHttpExchange(request)).toCompletableFuture())
                .andThen(chain.proceed(request));
    }

    private static class MicronautHttpExchange implements HttpExchange {

        private final HttpRequest<?> httpRequest;

        private MicronautHttpExchange(HttpRequest<?> httpRequest) {
            this.httpRequest = httpRequest;
        }

        @Override
        public String getRequestHeader(String headerName) {
            return this.httpRequest.getHeaders().get(headerName);
        }

        @Override
        public void setAttribute(String attributeName, Object attributeValue) {
            this.httpRequest.setAttribute(attributeName, attributeValue);
        }

    }

}

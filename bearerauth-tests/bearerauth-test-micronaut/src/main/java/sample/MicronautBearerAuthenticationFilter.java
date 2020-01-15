package sample;

import io.github.vpavic.bearerauth.AuthorizationContext;
import io.github.vpavic.bearerauth.BearerAuthenticationHandler;
import io.github.vpavic.bearerauth.BearerToken;
import io.github.vpavic.bearerauth.HttpExchange;
import io.github.vpavic.bearerauth.MapAuthorizationContextResolver;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.OncePerRequestHttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.reactivex.Completable;
import org.reactivestreams.Publisher;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Filter("/**")
public class MicronautBearerAuthenticationFilter extends OncePerRequestHttpServerFilter {

    private final BearerAuthenticationHandler bearerAuthenticationHandler;

    public MicronautBearerAuthenticationFilter() {
        Map<BearerToken, AuthorizationContext> authorizationContexts = new HashMap<>();
        authorizationContexts.put(new BearerToken("valid"),
                new AuthorizationContext(Collections.emptySet(), Instant.MAX, Collections.emptyMap()));
        authorizationContexts.put(new BearerToken("expired"),
                new AuthorizationContext(Collections.emptySet(), Instant.MIN, Collections.emptyMap()));
        this.bearerAuthenticationHandler = BearerAuthenticationHandler.builder(
                new MapAuthorizationContextResolver(authorizationContexts)).build();
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

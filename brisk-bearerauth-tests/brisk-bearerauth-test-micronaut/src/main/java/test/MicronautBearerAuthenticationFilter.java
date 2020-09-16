package test;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.OncePerRequestHttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.reactivex.Completable;
import org.briskidentity.bearerauth.BearerAuthenticationHandler;
import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.context.AuthorizationContextResolver;
import org.briskidentity.bearerauth.context.MapAuthorizationContextResolver;
import org.briskidentity.bearerauth.context.validation.AuthorizationContextValidator;
import org.briskidentity.bearerauth.context.validation.DefaultAuthorizationContextValidator;
import org.briskidentity.bearerauth.context.validation.ScopeMapping;
import org.briskidentity.bearerauth.http.HttpExchange;
import org.briskidentity.bearerauth.token.BearerToken;
import org.reactivestreams.Publisher;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Filter("/**")
public class MicronautBearerAuthenticationFilter extends OncePerRequestHttpServerFilter {

    private final BearerAuthenticationHandler bearerAuthenticationHandler;

    public MicronautBearerAuthenticationFilter() {
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
        this.bearerAuthenticationHandler = BearerAuthenticationHandler.builder(
                authorizationContextResolver).authorizationContextValidator(authorizationContextValidator).build();
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
        public String getRequestMethod() {
            return this.httpRequest.getMethod().name();
        }

        @Override
        public String getRequestPath() {
            return this.httpRequest.getPath();
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

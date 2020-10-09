package test;

import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.OncePerRequestHttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.reactivex.Flowable;
import org.briskidentity.bearerauth.BearerAuthenticationHandler;
import org.briskidentity.bearerauth.context.PropertiesAuthorizationContextResolver;
import org.briskidentity.bearerauth.http.ProtectedResourceRequest;
import org.briskidentity.bearerauth.http.WwwAuthenticateBuilder;
import org.briskidentity.bearerauth.token.error.BearerTokenException;
import org.reactivestreams.Publisher;

import java.io.IOException;

@Filter("/**")
public class MicronautBearerAuthenticationFilter extends OncePerRequestHttpServerFilter {

    private final BearerAuthenticationHandler bearerAuthenticationHandler;

    public MicronautBearerAuthenticationFilter() throws IOException {
        this.bearerAuthenticationHandler = BearerAuthenticationHandler.builder(
                new PropertiesAuthorizationContextResolver()).build();
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilterOnce(HttpRequest<?> request, ServerFilterChain chain) {
        return Flowable.fromFuture(
                this.bearerAuthenticationHandler.handle(new MicronautProtectedResourceRequest(request)).toCompletableFuture())
                .flatMap(authorizationContext -> {
                    request.setAttribute(HttpAttributes.PRINCIPAL, authorizationContext);
                    return chain.proceed(request);
                })
                .onErrorResumeNext(th -> {
                    Throwable cause = th.getCause();
                    if (!(cause instanceof BearerTokenException)) {
                        return Flowable.error(cause);
                    }
                    BearerTokenException ex = (BearerTokenException) cause;
                    String wwwAuthenticate = WwwAuthenticateBuilder.from(ex).build();
                    MutableHttpResponse<Object> response = HttpResponse.status(HttpStatus.valueOf(ex.getStatus()));
                    response.getHeaders().set(HttpHeaders.WWW_AUTHENTICATE, wwwAuthenticate);
                    return Flowable.just(response);
                });
    }

    private static class MicronautProtectedResourceRequest implements ProtectedResourceRequest {

        private final HttpRequest<?> httpRequest;

        private MicronautProtectedResourceRequest(HttpRequest<?> httpRequest) {
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
        public String getAuthorizationHeader() {
            return this.httpRequest.getHeaders().getAuthorization().orElse(null);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getNativeRequest() {
            return (T) this.httpRequest;
        }

    }

}

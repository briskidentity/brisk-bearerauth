package org.briskidentity.bearerauth;

import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.context.AuthorizationContextResolver;
import org.briskidentity.bearerauth.context.validation.AuthorizationContextValidator;
import org.briskidentity.bearerauth.context.validation.DefaultAuthorizationContextValidator;
import org.briskidentity.bearerauth.http.ProtectedResourceRequest;
import org.briskidentity.bearerauth.token.BearerToken;
import org.briskidentity.bearerauth.token.BearerTokenExtractor;
import org.briskidentity.bearerauth.token.error.BearerTokenError;
import org.briskidentity.bearerauth.token.error.BearerTokenException;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 *
 */
public class BearerAuthenticationHandler {

    private final BearerTokenExtractor bearerTokenExtractor;

    private final AuthorizationContextResolver authorizationContextResolver;

    private final AuthorizationContextValidator authorizationContextValidator;

    /**
     * @param bearerTokenExtractor the bearer token resolver
     * @param authorizationContextResolver the authorization context resolver
     * @param authorizationContextValidator the authorization context validator
     */
    private BearerAuthenticationHandler(BearerTokenExtractor bearerTokenExtractor,
            AuthorizationContextResolver authorizationContextResolver,
            AuthorizationContextValidator authorizationContextValidator) {
        Objects.requireNonNull(bearerTokenExtractor, "bearerTokenExtractor must not be null");
        Objects.requireNonNull(authorizationContextResolver, "authorizationContextResolver must not be null");
        Objects.requireNonNull(authorizationContextValidator, "authorizationContextValidator must not be null");
        this.bearerTokenExtractor = bearerTokenExtractor;
        this.authorizationContextResolver = authorizationContextResolver;
        this.authorizationContextValidator = authorizationContextValidator;
    }

    public static Builder builder(AuthorizationContextResolver authorizationContextResolver) {
        return new Builder(authorizationContextResolver);
    }

    /**
     * @param request the protected resource request
     * @return the completion stage of {@link AuthorizationContext}
     */
    public CompletionStage<AuthorizationContext> handle(ProtectedResourceRequest request) {
        BearerToken bearerToken = this.bearerTokenExtractor.extract(request);
        if (bearerToken == null) {
            CompletableFuture<AuthorizationContext> result = new CompletableFuture<>();
            result.completeExceptionally(new BearerTokenException());
            return result;
        }
        return this.authorizationContextResolver.resolve(bearerToken).handle((authorizationContext, throwable) -> {
            if (authorizationContext == null) {
                throw new BearerTokenException(BearerTokenError.INVALID_TOKEN);
            }
            this.authorizationContextValidator.validate(authorizationContext, request);
            return authorizationContext;
        });
    }

    public static class Builder {

        private final AuthorizationContextResolver authorizationContextResolver;

        private BearerTokenExtractor bearerTokenExtractor = BearerTokenExtractor.authorizationHeader();

        private AuthorizationContextValidator authorizationContextValidator = new DefaultAuthorizationContextValidator();

        private Builder(AuthorizationContextResolver authorizationContextResolver) {
            Objects.requireNonNull(authorizationContextResolver, "authorizationContextResolver must not be null");
            this.authorizationContextResolver = authorizationContextResolver;
        }

        public Builder bearerTokenExtractor(BearerTokenExtractor bearerTokenExtractor) {
            Objects.requireNonNull(bearerTokenExtractor, "bearerTokenExtractor must not be null");
            this.bearerTokenExtractor = bearerTokenExtractor;
            return this;
        }

        public Builder authorizationContextValidator(AuthorizationContextValidator authorizationContextValidator) {
            Objects.requireNonNull(authorizationContextValidator, "authorizationContextValidator must not be null");
            this.authorizationContextValidator = authorizationContextValidator;
            return this;
        }

        public BearerAuthenticationHandler build() {
            return new BearerAuthenticationHandler(this.bearerTokenExtractor, this.authorizationContextResolver,
                    this.authorizationContextValidator);
        }

    }

}

package org.briskidentity.bearerauth;

import org.briskidentity.bearerauth.context.AuthorizationContextResolver;
import org.briskidentity.bearerauth.context.validation.AuthorizationContextValidator;
import org.briskidentity.bearerauth.http.HttpExchange;
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

    public static final String AUTHORIZATION_CONTEXT_ATTRIBUTE = BearerAuthenticationHandler.class.getName() + ".AUTHORIZATION_CONTEXT";

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
     * @param httpExchange the HTTP exchange
     */
    public CompletionStage<Void> handle(HttpExchange httpExchange) {
        BearerToken bearerToken = this.bearerTokenExtractor.apply(httpExchange);
        if (bearerToken == null) {
            CompletableFuture<Void> result = new CompletableFuture<>();
            result.completeExceptionally(new BearerTokenException());
            return result;
        }
        return this.authorizationContextResolver.apply(bearerToken).handle((authorizationContext, throwable) -> {
            if (authorizationContext == null) {
                throw new BearerTokenException(BearerTokenError.INVALID_TOKEN);
            }
            this.authorizationContextValidator.accept(httpExchange, authorizationContext);
            httpExchange.setAttribute(AUTHORIZATION_CONTEXT_ATTRIBUTE, authorizationContext);
            return null;
        });
    }

    public static class Builder {

        private final AuthorizationContextResolver authorizationContextResolver;

        private BearerTokenExtractor bearerTokenExtractor = BearerTokenExtractor.authorizationHeader();

        private AuthorizationContextValidator authorizationContextValidator = AuthorizationContextValidator.expiry();

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

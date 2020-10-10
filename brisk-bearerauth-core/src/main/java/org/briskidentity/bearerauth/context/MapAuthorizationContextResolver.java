package org.briskidentity.bearerauth.context;

import org.briskidentity.bearerauth.token.BearerToken;
import org.briskidentity.bearerauth.token.error.BearerTokenError;
import org.briskidentity.bearerauth.token.error.BearerTokenException;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 *
 */
public class MapAuthorizationContextResolver implements AuthorizationContextResolver {

    private final Map<BearerToken, AuthorizationContext> authorizationContexts;

    public MapAuthorizationContextResolver(Map<BearerToken, AuthorizationContext> authorizationContexts) {
        Objects.requireNonNull(authorizationContexts, "authorizationContexts must not be null");
        this.authorizationContexts = authorizationContexts;
    }

    @Override
    public CompletionStage<AuthorizationContext> resolve(BearerToken bearerToken) {
        return CompletableFuture.supplyAsync(() -> {
            AuthorizationContext authorizationContext = this.authorizationContexts.get(bearerToken);
            if (authorizationContext == null) {
                throw new BearerTokenException(BearerTokenError.INVALID_TOKEN);
            }
            return authorizationContext;
        });
    }

}

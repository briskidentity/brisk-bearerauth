package org.briskidentity.bearerauth.context;

import org.briskidentity.bearerauth.token.BearerToken;

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
        return CompletableFuture.completedFuture(this.authorizationContexts.get(bearerToken));
    }

}

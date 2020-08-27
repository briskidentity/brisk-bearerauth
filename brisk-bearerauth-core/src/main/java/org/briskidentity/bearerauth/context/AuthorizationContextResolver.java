package org.briskidentity.bearerauth.context;

import org.briskidentity.bearerauth.token.BearerToken;

import java.util.concurrent.CompletionStage;

/**
 * A strategy used for resolving authorization context attached to a bearer access token.
 */
@FunctionalInterface
public interface AuthorizationContextResolver {

    /**
     * Resolve the authorization context from given bearer token.
     * @param bearerToken the bearer token
     * @return the authorization context
     */
    CompletionStage<AuthorizationContext> resolve(BearerToken bearerToken);

}

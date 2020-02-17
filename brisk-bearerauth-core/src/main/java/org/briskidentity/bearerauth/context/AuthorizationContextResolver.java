package org.briskidentity.bearerauth.context;

import org.briskidentity.bearerauth.token.BearerToken;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/**
 * A specialization of {@link Function} used for resolving authorization context attached to a bearer access token.
 */
@FunctionalInterface
public interface AuthorizationContextResolver extends Function<BearerToken, CompletionStage<AuthorizationContext>> {

}
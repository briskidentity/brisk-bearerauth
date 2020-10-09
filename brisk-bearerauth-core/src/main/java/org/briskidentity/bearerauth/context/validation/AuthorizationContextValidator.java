package org.briskidentity.bearerauth.context.validation;

import org.briskidentity.bearerauth.context.AuthorizationContext;

import java.util.concurrent.CompletionStage;

/**
 * A strategy for validating {@link AuthorizationContext}.
 *
 * @see AuthorizationContext
 */
@FunctionalInterface
public interface AuthorizationContextValidator {

    /**
     * Validate the authorization context.
     * @param authorizationContext the authorization context
     * @return the completion stage indicating validation outcome
     */
    CompletionStage<Void> validate(AuthorizationContext authorizationContext);

    /**
     * @return the expiry authorization context validator
     */
    static AuthorizationContextValidator expiry() {
        return ExpiryAuthorizationContextValidator.INSTANCE;
    }

}

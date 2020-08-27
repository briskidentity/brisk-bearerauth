package org.briskidentity.bearerauth.context.validation;

import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.http.HttpExchange;
import org.briskidentity.bearerauth.token.error.BearerTokenException;

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
     * @param httpExchange the HTTP exchange
     * @throws BearerTokenException if the authorization context is not valid
     */
    void validate(AuthorizationContext authorizationContext, HttpExchange httpExchange) throws BearerTokenException;

}

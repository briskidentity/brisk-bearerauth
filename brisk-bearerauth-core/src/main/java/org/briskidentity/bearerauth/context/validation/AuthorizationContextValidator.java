package org.briskidentity.bearerauth.context.validation;

import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.http.HttpExchange;

import java.util.function.BiConsumer;

/**
 * A specialization of {@link BiConsumer} used for validating {@link AuthorizationContext}.
 *
 * @see AuthorizationContext
 */
@FunctionalInterface
public interface AuthorizationContextValidator extends BiConsumer<HttpExchange, AuthorizationContext> {

}

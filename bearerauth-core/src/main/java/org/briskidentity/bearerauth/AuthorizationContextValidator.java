package org.briskidentity.bearerauth;

import java.util.function.BiConsumer;

/**
 * A specialization of {@link BiConsumer} used for validating {@link AuthorizationContext}.
 *
 * @see AuthorizationContext
 */
@FunctionalInterface
public interface AuthorizationContextValidator extends BiConsumer<HttpExchange, AuthorizationContext> {

    static AuthorizationContextValidator expiry() {
        return new ExpiryAuthorizationContextValidator();
    }

}

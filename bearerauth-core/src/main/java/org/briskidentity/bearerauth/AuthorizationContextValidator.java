package org.briskidentity.bearerauth;

import java.util.function.Consumer;

/**
 * A specialization of {@link Consumer} used for validating {@link AuthorizationContext}.
 *
 * @see AuthorizationContext
 */
@FunctionalInterface
public interface AuthorizationContextValidator extends Consumer<AuthorizationContext> {

    static AuthorizationContextValidator expiry() {
        return new ExpiryAuthorizationContextValidator();
    }

}

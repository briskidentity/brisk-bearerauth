package org.briskidentity.bearerauth;

import java.util.Arrays;
import java.util.List;
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

    static AuthorizationContextValidator composite(AuthorizationContextValidator... validators) {
        return new CompositeAuthorizationContextValidator(Arrays.asList(validators));
    }

}

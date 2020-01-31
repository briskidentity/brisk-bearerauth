package org.briskidentity.bearerauth.context.validation;

import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.http.HttpExchange;

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

    static AuthorizationContextValidator scope(List<ScopeMapping> scopeMappings) {
        return new ScopeAuthorizationContextValidator(scopeMappings);
    }

    static AuthorizationContextValidator composite(AuthorizationContextValidator... validators) {
        return new CompositeAuthorizationContextValidator(Arrays.asList(validators));
    }

}

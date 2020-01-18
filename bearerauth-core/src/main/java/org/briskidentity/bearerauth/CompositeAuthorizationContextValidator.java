package org.briskidentity.bearerauth;

import java.util.List;
import java.util.Objects;

/**
 *
 */
class CompositeAuthorizationContextValidator implements AuthorizationContextValidator {

    private final List<AuthorizationContextValidator> validators;

    /**
     * @param validators
     */
    CompositeAuthorizationContextValidator(List<AuthorizationContextValidator> validators) {
        Objects.requireNonNull(validators, "validators must not be null");
        this.validators = validators;
    }

    @Override
    public void accept(HttpExchange httpExchange, AuthorizationContext authorizationContext) {
        for (AuthorizationContextValidator validator : this.validators) {
            validator.accept(httpExchange, authorizationContext);
        }
    }

}

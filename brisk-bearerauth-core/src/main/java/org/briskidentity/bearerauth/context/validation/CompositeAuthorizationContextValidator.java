package org.briskidentity.bearerauth.context.validation;

import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.http.HttpExchange;

import java.util.List;
import java.util.Objects;

/**
 *
 */
public class CompositeAuthorizationContextValidator implements AuthorizationContextValidator {

    private final List<AuthorizationContextValidator> validators;

    /**
     * @param validators
     */
    public CompositeAuthorizationContextValidator(List<AuthorizationContextValidator> validators) {
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

package org.briskidentity.bearerauth.context.validation;

import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.token.error.BearerTokenError;
import org.briskidentity.bearerauth.token.error.BearerTokenException;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

class ScopeAuthorizationContextValidator implements AuthorizationContextValidator {

    private final Collection<String> requiredScopeValues;

    ScopeAuthorizationContextValidator(Collection<String> requiredScopeValues) {
        this.requiredScopeValues = Collections.unmodifiableCollection(requiredScopeValues);
    }

    @Override
    public CompletionStage<Void> validate(AuthorizationContext authorizationContext) {
        return CompletableFuture.supplyAsync(() -> {
            if (!authorizationContext.getScopeValues().containsAll(this.requiredScopeValues)) {
                throw new BearerTokenException(BearerTokenError.INSUFFICIENT_SCOPE);
            }
            return null;
        });
    }

}

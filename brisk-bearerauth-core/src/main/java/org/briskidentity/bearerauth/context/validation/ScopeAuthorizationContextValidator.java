package org.briskidentity.bearerauth.context.validation;

import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.http.HttpExchange;
import org.briskidentity.bearerauth.token.error.BearerTokenError;
import org.briskidentity.bearerauth.token.error.BearerTokenException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 *
 */
class ScopeAuthorizationContextValidator implements AuthorizationContextValidator {

    private final List<ScopeMapping> scopeMappings;

    ScopeAuthorizationContextValidator(List<ScopeMapping> scopeMappings) {
        Objects.requireNonNull(scopeMappings, "scopeMappings must not be null");
        this.scopeMappings = scopeMappings;
    }

    @Override
    public void accept(HttpExchange httpExchange, AuthorizationContext authorizationContext) {
        Set<String> requiredScopeValues = resolveRequiredScopeValues(httpExchange);
        if (!authorizationContext.getScopeValues().containsAll(requiredScopeValues)) {
            throw new BearerTokenException(BearerTokenError.INSUFFICIENT_SCOPE);
        }
    }

    private Set<String> resolveRequiredScopeValues(HttpExchange httpExchange) {
        for (ScopeMapping scopeMapping : this.scopeMappings) {
            // TODO implement Ant-style path patterns matching
            if (scopeMapping.getPath().equals(httpExchange.getRequestPath()) && (scopeMapping.getMethod() != null)
                    && scopeMapping.getMethod().equals(httpExchange.getRequestMethod())) {
                return scopeMapping.getScopeValues();
            }
        }
        return Collections.emptySet();
    }

}

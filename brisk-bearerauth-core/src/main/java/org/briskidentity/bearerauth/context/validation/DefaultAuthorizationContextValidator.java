package org.briskidentity.bearerauth.context.validation;

import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.http.HttpExchange;
import org.briskidentity.bearerauth.token.error.BearerTokenError;
import org.briskidentity.bearerauth.token.error.BearerTokenException;

import java.time.Clock;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DefaultAuthorizationContextValidator implements AuthorizationContextValidator {

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final List<ScopeMapping> scopeMappings;

    private AuthorizationContextValidator delegate;

    private Clock clock = Clock.systemUTC();

    private boolean denyIfNoMatch = true;

    public DefaultAuthorizationContextValidator(List<ScopeMapping> scopeMappings) {
        Objects.requireNonNull(scopeMappings, "scopeMappings must not be null");
        this.scopeMappings = scopeMappings;
    }

    public DefaultAuthorizationContextValidator() {
        this(Collections.emptyList());
    }

    public void setDelegate(AuthorizationContextValidator delegate) {
        this.delegate = delegate;
    }

    public void setClock(Clock clock) {
        Objects.requireNonNull(clock, "clock must not be null");
        this.clock = clock;
    }

    public void setDenyIfNoMatch(boolean denyIfNoMatch) {
        this.denyIfNoMatch = denyIfNoMatch;
    }

    @Override
    public void validate(AuthorizationContext authorizationContext, HttpExchange httpExchange) {
        if (this.clock.instant().isAfter(authorizationContext.getExpiry())) {
            throw new BearerTokenException(BearerTokenError.INVALID_TOKEN);
        }
        if (!hasRequiredScope(httpExchange, authorizationContext.getScopeValues())) {
            throw new BearerTokenException(BearerTokenError.INSUFFICIENT_SCOPE);
        }
        if (this.delegate != null) {
            this.delegate.validate(authorizationContext, httpExchange);
        }
    }

    private boolean hasRequiredScope(HttpExchange httpExchange, Set<String> presentedScopeValues) {
        for (ScopeMapping scopeMapping : this.scopeMappings) {
            if (pathMatches(scopeMapping, httpExchange) && methodMatches(scopeMapping, httpExchange)
                    && presentedScopeValues.containsAll(scopeMapping.getScopeValues())) {
                return true;
            }
        }
        return !this.denyIfNoMatch;
    }

    private static boolean pathMatches(ScopeMapping scopeMapping, HttpExchange httpExchange) {
        return antPathMatcher.matches(scopeMapping.getPathPattern(), httpExchange.getRequestPath());
    }

    private static boolean methodMatches(ScopeMapping scopeMapping, HttpExchange httpExchange) {
        return (scopeMapping.getMethod() != null) && scopeMapping.getMethod().equals(httpExchange.getRequestMethod());
    }

}

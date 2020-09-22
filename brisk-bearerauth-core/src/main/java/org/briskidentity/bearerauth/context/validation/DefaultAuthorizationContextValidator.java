package org.briskidentity.bearerauth.context.validation;

import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.http.ProtectedResourceRequest;
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
    public void validate(AuthorizationContext authorizationContext, ProtectedResourceRequest request) {
        if (this.clock.instant().isAfter(authorizationContext.getExpiry())) {
            throw new BearerTokenException(BearerTokenError.INVALID_TOKEN);
        }
        if (!hasRequiredScope(request, authorizationContext.getScopeValues())) {
            throw new BearerTokenException(BearerTokenError.INSUFFICIENT_SCOPE);
        }
        if (this.delegate != null) {
            this.delegate.validate(authorizationContext, request);
        }
    }

    private boolean hasRequiredScope(ProtectedResourceRequest request, Set<String> presentedScopeValues) {
        for (ScopeMapping scopeMapping : this.scopeMappings) {
            if (pathMatches(scopeMapping, request) && methodMatches(scopeMapping, request)
                    && presentedScopeValues.containsAll(scopeMapping.getScopeValues())) {
                return true;
            }
        }
        return !this.denyIfNoMatch;
    }

    private static boolean pathMatches(ScopeMapping scopeMapping, ProtectedResourceRequest request) {
        return antPathMatcher.matches(scopeMapping.getPathPattern(), request.getRequestPath());
    }

    private static boolean methodMatches(ScopeMapping scopeMapping, ProtectedResourceRequest request) {
        return (scopeMapping.getMethod() != null) && scopeMapping.getMethod().equals(request.getRequestMethod());
    }

}

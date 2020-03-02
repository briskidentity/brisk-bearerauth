package org.briskidentity.bearerauth.context.validation;

import java.util.Objects;
import java.util.Set;

/**
 *
 */
public final class ScopeMapping {

    private final String pathPattern;

    private final String method;

    private final Set<String> scopeValues;

    /**
     * @param pathPattern
     * @param method
     * @param scopeValues
     */
    public ScopeMapping(String pathPattern, String method, Set<String> scopeValues) {
        Objects.requireNonNull(pathPattern, "pathPattern must not be null");
        Objects.requireNonNull(method, "method must not be null");
        Objects.requireNonNull(scopeValues, "scopeValues must not be null");
        this.pathPattern = pathPattern;
        this.method = method;
        this.scopeValues = scopeValues;
    }

    /**
     * @return
     */
    public String getPathPattern() {
        return this.pathPattern;
    }

    /**
     * @return
     */
    public String getMethod() {
        return this.method;
    }

    /**
     * @return
     */
    public Set<String> getScopeValues() {
        return this.scopeValues;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ScopeMapping that = (ScopeMapping) obj;
        return this.pathPattern.equals(that.pathPattern) && this.method.equals(that.method)
                && this.scopeValues.equals(that.scopeValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pathPattern, this.method, this.scopeValues);
    }

}

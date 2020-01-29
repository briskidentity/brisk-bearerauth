package org.briskidentity.bearerauth;

import java.util.Objects;
import java.util.Set;

/**
 *
 */
public final class ScopeMapping {

    private final String path;

    private final String method;

    private final Set<String> scopeValues;

    /**
     * @param path
     * @param method
     * @param scopeValues
     */
    public ScopeMapping(String path, String method, Set<String> scopeValues) {
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(method, "method must not be null");
        Objects.requireNonNull(scopeValues, "scopeValues must not be null");
        this.path = path;
        this.method = method;
        this.scopeValues = scopeValues;
    }

    /**
     * @return
     */
    public String getPath() {
        return this.path;
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
        return this.path.equals(that.path) && this.method.equals(that.method)
                && this.scopeValues.equals(that.scopeValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.path, this.method, this.scopeValues);
    }

}

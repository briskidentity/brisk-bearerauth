package io.github.vpavic.bearerauth;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class AuthorizationContext {

    private final Set<String> scope;

    private final Instant expiry;

    private final Map<String, Object> attributes;

    /**
     * @param scope the scope
     * @param expiry the expiry
     * @param attributes the attributes
     */
    public AuthorizationContext(Set<String> scope, Instant expiry, Map<String, Object> attributes) {
        this.scope = Collections.unmodifiableSet(scope);
        this.expiry = expiry;
        this.attributes = Collections.unmodifiableMap(attributes);
    }

    /**
     * @return the scope
     */
    public Set<String> getScope() {
        return this.scope;
    }

    /**
     * @return the expiry
     */
    public Instant getExpiry() {
        return this.expiry;
    }

    /**
     * @return the attributes
     */
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    /**
     * @param name
     * @param <T>
     * @return
     */
    public <T> T getAttribute(String name) {
        @SuppressWarnings("unchecked")
        T attribute = (T) this.attributes.get(name);
        return attribute;
    }

}

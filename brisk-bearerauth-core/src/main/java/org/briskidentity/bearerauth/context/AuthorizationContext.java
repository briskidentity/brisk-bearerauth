package org.briskidentity.bearerauth.context;

import org.briskidentity.bearerauth.token.BearerToken;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Representation of authorization context attached to a {@link BearerToken}.
 */
public class AuthorizationContext implements Serializable {

    private final Set<String> scopeValues;

    private final Instant expiry;

    private final Map<String, Object> attributes;

    /**
     * @param scopeValues the scope values
     * @param expiry the expiry
     * @param attributes the attributes
     */
    public AuthorizationContext(Set<String> scopeValues, Instant expiry, Map<String, Object> attributes) {
        this.scopeValues = Collections.unmodifiableSet(scopeValues);
        this.expiry = expiry;
        this.attributes = Collections.unmodifiableMap(attributes);
    }

    /**
     * Get the authorization context scope values.
     * @return the scope values
     */
    public Set<String> getScopeValues() {
        return this.scopeValues;
    }

    /**
     * Get the authorization context expiry.
     * @return the expiry
     */
    public Instant getExpiry() {
        return this.expiry;
    }

    /**
     * Get the authorization context attributes.
     * @return the attributes
     */
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    /**
     * Get the authorization context attribute by name.
     * @param name the attribute name
     * @param <T> the attribute type
     * @return the attribute value
     */
    public <T> T getAttribute(String name) {
        @SuppressWarnings("unchecked")
        T attribute = (T) this.attributes.get(name);
        return attribute;
    }

}

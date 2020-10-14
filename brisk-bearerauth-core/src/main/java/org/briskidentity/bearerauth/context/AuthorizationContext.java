package org.briskidentity.bearerauth.context;

import org.briskidentity.bearerauth.token.BearerToken;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Supplier;

/**
 * Representation of authorization context attached to a {@link BearerToken}.
 */
public class AuthorizationContext implements Principal, Serializable {

    private final Set<String> scopeValues;

    private final Map<String, Object> attributes;

    private final String principalName;

    /**
     * @param scopeValues the scope values
     * @param attributes the attributes
     * @param principalNameSupplier the principal name supplier
     */
    public AuthorizationContext(Set<String> scopeValues, Map<String, Object> attributes,
            Supplier<String> principalNameSupplier) {
        Objects.requireNonNull(scopeValues, "scopeValues must not be null");
        Objects.requireNonNull(attributes, "attributes must not be null");
        Objects.requireNonNull(principalNameSupplier, "principalNameSupplier must not be null");
        this.scopeValues = Collections.unmodifiableSet(scopeValues);
        this.attributes = Collections.unmodifiableMap(attributes);
        this.principalName = principalNameSupplier.get();
    }

    /**
     * @param scopeValues the scope values
     * @param attributes the attributes
     */
    public AuthorizationContext(Set<String> scopeValues, Map<String, Object> attributes) {
        this(scopeValues, attributes, () -> (String) attributes.get("sub"));
    }

    /**
     * Get the authorization context scope values.
     * @return the scope values
     */
    public Set<String> getScopeValues() {
        return this.scopeValues;
    }

    /**
     * Get the authorization context attributes.
     * @return the attributes
     */
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getName() {
        return this.principalName;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AuthorizationContext.class.getSimpleName() + "[", "]")
                .add("principalName='" + this.principalName + "'")
                .add("scopeValues='" + String.join(" ", this.scopeValues) + "'").toString();
    }

}

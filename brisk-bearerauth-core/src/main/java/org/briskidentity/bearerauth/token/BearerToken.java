package org.briskidentity.bearerauth.token;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representation of bearer token.
 */
public class BearerToken implements Serializable {

    private String value;

    public BearerToken(String value) {
        Objects.requireNonNull(value, "value must not be null");
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BearerToken that = (BearerToken) obj;
        return this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String toString() {
        return this.value;
    }

}

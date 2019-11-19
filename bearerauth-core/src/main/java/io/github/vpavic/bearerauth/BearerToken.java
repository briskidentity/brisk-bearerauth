package io.github.vpavic.bearerauth;

import java.util.Objects;

/**
 *
 */
public class BearerToken {

    private String value;

    public BearerToken(String value) {
        Objects.requireNonNull(value, "value must not be null");
        this.value = value;
    }

    /**
     * @return
     */
    public String getValue() {
        return this.value;
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
        return Objects.hash(this.value);
    }

}

package org.briskidentity.bearerauth.token.error;

import java.util.Objects;

public class BearerTokenException extends RuntimeException {

    private final BearerTokenError error;

    public BearerTokenException() {
        this.error = null;
    }

    public BearerTokenException(String message) {
        super(message);
        this.error = null;
    }

    public BearerTokenException(BearerTokenError error, String message) {
        super(message);
        Objects.requireNonNull(error, "error must not be null");
        this.error = error;
    }

    public BearerTokenException(BearerTokenError error) {
        this(error, error.getCode());
    }

    public int getStatus() {
        return (this.error != null) ? this.error.getStatus() : 401;
    }

    public BearerTokenError getError() {
        return this.error;
    }

}

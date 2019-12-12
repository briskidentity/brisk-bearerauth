package io.github.vpavic.bearerauth;

import java.util.Objects;

public class BearerTokenException extends RuntimeException {

    private final BearerTokenError error;

    public BearerTokenException(BearerTokenError error, String message) {
        super(message);
        Objects.requireNonNull(error, "error must not be null");
        this.error = error;
    }

    public BearerTokenException(BearerTokenError error) {
        this(error, error.getCode());
    }

    public BearerTokenError getError() {
        return this.error;
    }

}

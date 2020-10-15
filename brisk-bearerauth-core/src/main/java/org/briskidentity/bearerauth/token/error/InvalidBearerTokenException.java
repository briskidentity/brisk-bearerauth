package org.briskidentity.bearerauth.token.error;

public class InvalidBearerTokenException extends BearerTokenException {

    public InvalidBearerTokenException(String message) {
        super("invalid_token", 401, message);
    }

    public InvalidBearerTokenException() {
        this(null);
    }

}

package org.briskidentity.bearerauth.token.error;

public class MissingBearerTokenException extends BearerTokenException {

    public MissingBearerTokenException(String message) {
        super(null, 401, message);
    }

    public MissingBearerTokenException() {
        this(null);
    }

}

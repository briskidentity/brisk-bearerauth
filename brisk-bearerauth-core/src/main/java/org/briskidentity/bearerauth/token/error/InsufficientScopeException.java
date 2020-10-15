package org.briskidentity.bearerauth.token.error;

public class InsufficientScopeException extends BearerTokenException {

    public InsufficientScopeException(String message) {
        super("insufficient_scope", 403, message);
    }

    public InsufficientScopeException() {
        this(null);
    }

}

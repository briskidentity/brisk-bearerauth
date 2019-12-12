package io.github.vpavic.bearerauth;

import java.util.Objects;

public class BearerTokenError {

    public static final BearerTokenError INVALID_REQUEST = new BearerTokenError(400, "invalid_request");

    public static final BearerTokenError INVALID_TOKEN = new BearerTokenError(401, "invalid_token");

    public static final BearerTokenError INSUFFICIENT_SCOPE = new BearerTokenError(403, "insufficient_scope");

    private final int status;

    private final String code;

    public BearerTokenError(int status, String code) {
        Objects.requireNonNull(code, "code must not be null");
        this.status = status;
        this.code = code;
    }

    public int getStatus() {
        return this.status;
    }

    public String getCode() {
        return this.code;
    }

}

package org.briskidentity.bearerauth;

import java.util.Objects;

public class BearerTokenError {

    public static final BearerTokenError INVALID_REQUEST = BearerTokenError.of("invalid_request", 400);

    public static final BearerTokenError INVALID_TOKEN = BearerTokenError.of("invalid_token", 401);

    public static final BearerTokenError INSUFFICIENT_SCOPE = BearerTokenError.of("insufficient_scope", 403);

    private final String code;

    private final int status;

    private BearerTokenError(String code, int status) {
        Objects.requireNonNull(code, "code must not be null");
        this.code = code;
        this.status = status;
    }

    public static BearerTokenError of(String code, int status) {
        return new BearerTokenError(code, status);
    }

    public String getCode() {
        return this.code;
    }

    public int getStatus() {
        return this.status;
    }

}

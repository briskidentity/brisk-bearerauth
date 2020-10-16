package org.briskidentity.bearerauth.token.error;

public class BearerTokenError {

    public static final BearerTokenError NO_AUTHENTICATION = BearerTokenError.of(null, 401);

    public static final BearerTokenError INVALID_REQUEST = BearerTokenError.of("invalid_request", 400);

    public static final BearerTokenError INVALID_TOKEN = BearerTokenError.of("invalid_token", 401);

    public static final BearerTokenError INSUFFICIENT_SCOPE = BearerTokenError.of("insufficient_scope", 403);

    private final String errorCode;

    private final int httpStatus;

    private BearerTokenError(String errorCode, int httpStatus) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public static BearerTokenError of(String errorCode, int httpStatus) {
        return new BearerTokenError(errorCode, httpStatus);
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public int getHttpStatus() {
        return this.httpStatus;
    }

}

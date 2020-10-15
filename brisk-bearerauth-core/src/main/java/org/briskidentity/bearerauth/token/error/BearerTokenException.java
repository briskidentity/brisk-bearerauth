package org.briskidentity.bearerauth.token.error;

public abstract class BearerTokenException extends RuntimeException {

    private final String errorCode;

    private final int httpStatus;

    protected BearerTokenException(String errorCode, int httpStatus, String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public int getHttpStatus() {
        return this.httpStatus;
    }

}

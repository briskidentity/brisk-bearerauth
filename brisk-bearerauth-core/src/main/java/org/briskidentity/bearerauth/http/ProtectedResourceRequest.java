package org.briskidentity.bearerauth.http;

/**
 * A representation of protected resource request abstraction.
 */
public interface ProtectedResourceRequest {

    /**
     * Get the first occurrence of request's authorization header.
     * @return the authorization header value
     */
    String getAuthorizationHeader();

    /**
     * Get the native request.
     * @return the native request
     */
    <T> T getNativeRequest();

}

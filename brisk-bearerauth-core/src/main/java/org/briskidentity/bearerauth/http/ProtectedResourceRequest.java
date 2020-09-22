package org.briskidentity.bearerauth.http;

/**
 * A representation of protected resource request abstraction.
 */
public interface ProtectedResourceRequest {

    /**
     * Get the request method.
     * @return the request method
     */
    String getRequestMethod();

    /**
     * Get the request path.
     * @return the request path
     */
    String getRequestPath();

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

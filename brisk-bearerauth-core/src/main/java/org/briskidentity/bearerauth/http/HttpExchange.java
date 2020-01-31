package org.briskidentity.bearerauth.http;

/**
 * HTTP exchange abstraction.
 */
public interface HttpExchange {

    /**
     * Get the first occurrence of HTTP request header.
     * @param headerName the header name
     * @return the header value
     */
    String getRequestHeader(String headerName);

    /**
     * Get the HTTP request method.
     * @return the request method
     */
    String getRequestMethod();

    /**
     * Get the HTTP request path.
     * @return the request path
     */
    String getRequestPath();

    /**
     * Set the HTTP exchange attribute.
     * @param attributeName  the attribute name
     * @param attributeValue the attribute value
     */
    void setAttribute(String attributeName, Object attributeValue);

}

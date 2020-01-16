package org.briskidentity.bearerauth;

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
     * Set the HTTP exchange attribute.
     * @param attributeName  the attribute name
     * @param attributeValue the attribute value
     */
    void setAttribute(String attributeName, Object attributeValue);

}

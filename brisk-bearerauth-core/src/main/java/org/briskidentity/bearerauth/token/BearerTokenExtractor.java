package org.briskidentity.bearerauth.token;

import org.briskidentity.bearerauth.http.HttpExchange;

/**
 * A strategy used for extracting bearer token from HTTP request.
 */
@FunctionalInterface
public interface BearerTokenExtractor {

    /**
     * Extract the bearer token from given HTTP exchange.
     * @param httpExchange the HTTP exchange
     * @return the bearer token
     */
    BearerToken extract(HttpExchange httpExchange);

    /**
     * @return the authorization header bearer token extractor
     */
    static BearerTokenExtractor authorizationHeader() {
        return AuthorizationHeaderBearerTokenExtractor.INSTANCE;
    }

}

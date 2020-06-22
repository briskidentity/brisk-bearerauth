package org.briskidentity.bearerauth.token;

import org.briskidentity.bearerauth.http.HttpExchange;

import java.util.Objects;

/**
 * A {@link BearerTokenExtractor} implementation that extracts token from {@code Authorization} HTTP request header.
 */
class AuthorizationHeaderBearerTokenExtractor implements BearerTokenExtractor {

    static final AuthorizationHeaderBearerTokenExtractor INSTANCE = new AuthorizationHeaderBearerTokenExtractor();

    private AuthorizationHeaderBearerTokenExtractor() {
    }

    @Override
    public BearerToken apply(HttpExchange httpExchange) {
        Objects.requireNonNull(httpExchange, "httpExchange must not be null");
        String authorizationHeader = httpExchange.getRequestHeader("Authorization");
        if ((authorizationHeader == null) || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return new BearerToken(authorizationHeader.substring(7));
    }

}

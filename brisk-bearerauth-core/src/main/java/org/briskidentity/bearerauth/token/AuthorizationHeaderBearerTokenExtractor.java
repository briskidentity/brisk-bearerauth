package org.briskidentity.bearerauth.token;

import org.briskidentity.bearerauth.http.ProtectedResourceRequest;
import org.briskidentity.bearerauth.token.error.BearerTokenError;
import org.briskidentity.bearerauth.token.error.BearerTokenException;

import java.util.Objects;

/**
 * A {@link BearerTokenExtractor} implementation that extracts token from {@code Authorization} HTTP request header.
 */
class AuthorizationHeaderBearerTokenExtractor implements BearerTokenExtractor {

    static final AuthorizationHeaderBearerTokenExtractor INSTANCE = new AuthorizationHeaderBearerTokenExtractor();

    private AuthorizationHeaderBearerTokenExtractor() {
    }

    @Override
    public BearerToken extract(ProtectedResourceRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        String authorizationHeader = request.getAuthorizationHeader();
        if ((authorizationHeader == null) || !authorizationHeader.startsWith("Bearer ")) {
            throw new BearerTokenException(BearerTokenError.NO_AUTHENTICATION);
        }
        return new BearerToken(authorizationHeader.substring(7));
    }

}

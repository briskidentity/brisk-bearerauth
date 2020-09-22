package org.briskidentity.bearerauth.token;

import org.briskidentity.bearerauth.http.ProtectedResourceRequest;

/**
 * A strategy used for extracting bearer token from the protected resource request.
 */
@FunctionalInterface
public interface BearerTokenExtractor {

    /**
     * Extract the bearer token from the given protected resource request.
     * @param request the protected resource request
     * @return the bearer token
     */
    BearerToken extract(ProtectedResourceRequest request);

    /**
     * @return the authorization header bearer token extractor
     */
    static BearerTokenExtractor authorizationHeader() {
        return AuthorizationHeaderBearerTokenExtractor.INSTANCE;
    }

}

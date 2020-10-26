/*
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

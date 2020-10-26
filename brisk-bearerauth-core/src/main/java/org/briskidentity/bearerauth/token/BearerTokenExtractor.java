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
import org.briskidentity.bearerauth.token.error.BearerTokenException;

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
	BearerToken extract(ProtectedResourceRequest request) throws BearerTokenException;

	/**
	 * @return the authorization header bearer token extractor
	 */
	static BearerTokenExtractor authorizationHeader() {
		return AuthorizationHeaderBearerTokenExtractor.INSTANCE;
	}

}

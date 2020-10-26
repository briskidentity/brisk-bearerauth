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

import org.junit.jupiter.api.Test;

import org.briskidentity.bearerauth.http.ProtectedResourceRequest;
import org.briskidentity.bearerauth.token.error.BearerTokenException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * Tests for {@link AuthorizationHeaderBearerTokenExtractor}.
 */
class AuthorizationHeaderBearerTokenExtractorTests {

	private final BearerTokenExtractor bearerTokenExtractor = AuthorizationHeaderBearerTokenExtractor.INSTANCE;

	@Test
	void extract_NullRequest_ShouldThrowException() {
		assertThatNullPointerException().isThrownBy(() -> this.bearerTokenExtractor.extract(null))
				.withMessage("request must not be null");
	}

	@Test
	void extract_RequestWithNoAuthorizationHeader_ShouldReturnNull() {
		assertThatExceptionOfType(BearerTokenException.class)
				.isThrownBy(() -> this.bearerTokenExtractor.extract(new TestProtectedResourceRequest(null)));
	}

	@Test
	void extract_RequestWithUnsupportedAuthorizationType_ShouldReturnNull() {
		assertThatExceptionOfType(BearerTokenException.class)
				.isThrownBy(() -> this.bearerTokenExtractor.extract(new TestProtectedResourceRequest("Basic test")));
	}

	@Test
	void extract_RequestWithLowercaseBearerAuthorizationType_ShouldReturnNull() {
		assertThatExceptionOfType(BearerTokenException.class)
				.isThrownBy(() -> this.bearerTokenExtractor.extract(new TestProtectedResourceRequest("bearer test")));
	}

	@Test
	void extract_RequestWithBearerAuthorizationType_ShouldReturnToken() {
		assertThat(this.bearerTokenExtractor.extract(new TestProtectedResourceRequest("Bearer test")))
				.isEqualTo(new BearerToken("test"));
	}

	private static class TestProtectedResourceRequest implements ProtectedResourceRequest {

		private final String authorizationHeaderValue;

		private TestProtectedResourceRequest(String authorizationHeaderValue) {
			this.authorizationHeaderValue = authorizationHeaderValue;
		}

		@Override
		public String getAuthorizationHeader() {
			return this.authorizationHeaderValue;
		}

		@Override
		public <T> T getNativeRequest() {
			throw new UnsupportedOperationException();
		}

	}

}

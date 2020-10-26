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

package org.briskidentity.bearerauth.http;

import org.junit.jupiter.api.Test;

import org.briskidentity.bearerauth.token.error.BearerTokenError;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * Tests for {@link WwwAuthenticateBuilder}.
 */
class WwwAuthenticateBuilderTests {

	@Test
	void build_NullError_ShouldThrowNullPointerException() {
		assertThatNullPointerException().isThrownBy(() -> WwwAuthenticateBuilder.from(null).build())
				.withMessage("bearerTokenError must not be null");
	}

	@Test
	void build_BearerTokenErrorWithNoCode_ShouldReturnWwwAuthenticateWithNoError() {
		String wwwAuthenticate = WwwAuthenticateBuilder.from(BearerTokenError.NO_AUTHENTICATION).build();
		assertThat(wwwAuthenticate).isEqualTo("Bearer");
	}

	@Test
	void build_BearerTokenErrorWithCode_ShouldReturnWwwAuthenticateWithError() {
		String wwwAuthenticate = WwwAuthenticateBuilder.from(BearerTokenError.INVALID_TOKEN).build();
		assertThat(wwwAuthenticate).isEqualTo("Bearer error=\"invalid_token\"");
	}

	@Test
	void build_WithRealmAndBearerTokenErrorWithNoCode_ShouldReturnWwwAuthenticateWithRealmAndNoError() {
		String wwwAuthenticate = WwwAuthenticateBuilder.from(BearerTokenError.NO_AUTHENTICATION).withRealm("example")
				.build();
		assertThat(wwwAuthenticate).isEqualTo("Bearer realm=\"example\"");
	}

	@Test
	void build_WithRealmAndBearerTokenErrorWithCode_ShouldReturnWwwAuthenticateWithRealmAndError() {
		String wwwAuthenticate = WwwAuthenticateBuilder.from(BearerTokenError.INVALID_TOKEN).withRealm("example")
				.build();
		assertThat(wwwAuthenticate).isEqualTo("Bearer realm=\"example\", error=\"invalid_token\"");
	}

}

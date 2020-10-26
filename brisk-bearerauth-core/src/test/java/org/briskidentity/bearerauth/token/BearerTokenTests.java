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

import java.io.Serializable;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * Tests for {@link BearerToken}.
 */
class BearerTokenTests {

	@Test
	void constructor_NullValue_ShouldThrowException() {
		assertThatNullPointerException().isThrownBy(() -> new BearerToken(null)).withMessage("value must not be null");
	}

	@Test
	@SuppressWarnings("ConstantConditions")
	void isSerializable_ValidValue_ShouldReturnTrue() {
		assertThat(new BearerToken("test") instanceof Serializable).isTrue();
	}

	@Test
	@SuppressWarnings("EqualsWithItself")
	void equals_SameToken_ShouldReturnTrue() {
		BearerToken bearerToken = new BearerToken("test");
		assertThat(bearerToken.equals(bearerToken)).isTrue();
	}

	@Test
	void equals_TokenWithSameValue_ShouldReturnTrue() {
		assertThat(new BearerToken("test").equals(new BearerToken("test"))).isTrue();
	}

	@Test
	void equals_TokenWithDifferentValue_ShouldReturnFalse() {
		assertThat(new BearerToken("test").equals(new BearerToken("abcd"))).isFalse();
	}

	@Test
	@SuppressWarnings("EqualsBetweenInconvertibleTypes")
	void equals_StringWithSameValue_ShouldReturnFalse() {
		assertThat(new BearerToken("test").equals("test")).isFalse();
	}

}

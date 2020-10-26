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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.briskidentity.bearerauth.token.error.BearerTokenError;

/**
 * Helper for building {@code WWW-Authenticate} header value.
 */
public final class WwwAuthenticateBuilder {

	private final BearerTokenError bearerTokenError;

	private String realm;

	private WwwAuthenticateBuilder(BearerTokenError bearerTokenError) {
		Objects.requireNonNull(bearerTokenError, "bearerTokenError must not be null");
		this.bearerTokenError = bearerTokenError;
	}

	/**
	 * Create a builder instance for given bearer token error.
	 * @param bearerTokenError the bearer token error
	 * @return the builder instance
	 */
	public static WwwAuthenticateBuilder from(BearerTokenError bearerTokenError) {
		return new WwwAuthenticateBuilder(bearerTokenError);
	}

	/**
	 * Set the realm to use.
	 * @param realm the realm
	 * @return the current builder
	 */
	public WwwAuthenticateBuilder withRealm(String realm) {
		Objects.requireNonNull(realm, "realm must not be null");
		this.realm = realm;
		return this;
	}

	/**
	 * Build the {@code WWW-Authenticate} header value.
	 * @return the {@code WWW-Authenticate} header value
	 */
	public String build() {
		String wwwAuthenticate = "Bearer";
		List<String> attributes = new ArrayList<>();
		if (this.realm != null) {
			attributes.add(buildAttribute("realm", this.realm));
		}
		String errorCode = this.bearerTokenError.getErrorCode();
		if (errorCode != null) {
			attributes.add(buildAttribute("error", errorCode));
		}
		if (!attributes.isEmpty()) {
			wwwAuthenticate += " " + String.join(", ", attributes);
		}
		return wwwAuthenticate;
	}

	private static String buildAttribute(String name, String value) {
		return name + "=\"" + value + "\"";
	}

}

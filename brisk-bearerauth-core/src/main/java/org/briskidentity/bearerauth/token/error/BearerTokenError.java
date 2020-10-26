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

package org.briskidentity.bearerauth.token.error;

public class BearerTokenError {

	public static final BearerTokenError NO_AUTHENTICATION = BearerTokenError.of(null, 401);

	public static final BearerTokenError INVALID_REQUEST = BearerTokenError.of("invalid_request", 400);

	public static final BearerTokenError INVALID_TOKEN = BearerTokenError.of("invalid_token", 401);

	public static final BearerTokenError INSUFFICIENT_SCOPE = BearerTokenError.of("insufficient_scope", 403);

	private final String errorCode;

	private final int httpStatus;

	private BearerTokenError(String errorCode, int httpStatus) {
		this.errorCode = errorCode;
		this.httpStatus = httpStatus;
	}

	public static BearerTokenError of(String errorCode, int httpStatus) {
		return new BearerTokenError(errorCode, httpStatus);
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public int getHttpStatus() {
		return this.httpStatus;
	}

}

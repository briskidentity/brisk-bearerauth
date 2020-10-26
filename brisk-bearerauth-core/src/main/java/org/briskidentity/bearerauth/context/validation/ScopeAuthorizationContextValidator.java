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

package org.briskidentity.bearerauth.context.validation;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.token.error.BearerTokenError;
import org.briskidentity.bearerauth.token.error.BearerTokenException;

class ScopeAuthorizationContextValidator implements AuthorizationContextValidator {

	private final Collection<String> requiredScopeValues;

	ScopeAuthorizationContextValidator(Collection<String> requiredScopeValues) {
		this.requiredScopeValues = Collections.unmodifiableCollection(requiredScopeValues);
	}

	@Override
	public CompletionStage<Void> validate(AuthorizationContext authorizationContext) {
		return CompletableFuture.supplyAsync(() -> {
			if (!authorizationContext.getScopeValues().containsAll(this.requiredScopeValues)) {
				throw new BearerTokenException(BearerTokenError.INSUFFICIENT_SCOPE);
			}
			return null;
		});
	}

}

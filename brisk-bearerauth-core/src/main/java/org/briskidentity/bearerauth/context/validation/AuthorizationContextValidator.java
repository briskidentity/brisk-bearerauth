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

import org.briskidentity.bearerauth.context.AuthorizationContext;

import java.util.Collection;
import java.util.concurrent.CompletionStage;

/**
 * A strategy for validating {@link AuthorizationContext}.
 *
 * @see AuthorizationContext
 */
@FunctionalInterface
public interface AuthorizationContextValidator {

    /**
     * Validate the authorization context.
     * @param authorizationContext the authorization context
     * @return the completion stage indicating validation outcome
     */
    CompletionStage<Void> validate(AuthorizationContext authorizationContext);

    static AuthorizationContextValidator scope(Collection<String> requiredScopeValues) {
        return new ScopeAuthorizationContextValidator(requiredScopeValues);
    }

}

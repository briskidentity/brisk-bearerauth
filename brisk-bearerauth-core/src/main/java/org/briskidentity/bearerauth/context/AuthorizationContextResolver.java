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

package org.briskidentity.bearerauth.context;

import org.briskidentity.bearerauth.token.BearerToken;

import java.util.concurrent.CompletionStage;

/**
 * A strategy used for resolving authorization context attached to a bearer access token.
 */
@FunctionalInterface
public interface AuthorizationContextResolver {

    /**
     * Resolve the authorization context from given bearer token.
     * @param bearerToken the bearer token
     * @return the authorization context
     */
    CompletionStage<AuthorizationContext> resolve(BearerToken bearerToken);

}

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
import org.briskidentity.bearerauth.token.error.BearerTokenError;
import org.briskidentity.bearerauth.token.error.BearerTokenException;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 *
 */
public class MapAuthorizationContextResolver implements AuthorizationContextResolver {

    private final Map<BearerToken, AuthorizationContext> authorizationContexts;

    public MapAuthorizationContextResolver(Map<BearerToken, AuthorizationContext> authorizationContexts) {
        Objects.requireNonNull(authorizationContexts, "authorizationContexts must not be null");
        this.authorizationContexts = authorizationContexts;
    }

    @Override
    public CompletionStage<AuthorizationContext> resolve(BearerToken bearerToken) {
        return CompletableFuture.supplyAsync(() -> {
            AuthorizationContext authorizationContext = this.authorizationContexts.get(bearerToken);
            if (authorizationContext == null) {
                throw new BearerTokenException(BearerTokenError.INVALID_TOKEN);
            }
            return authorizationContext;
        });
    }

}

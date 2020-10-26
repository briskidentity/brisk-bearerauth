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

/**
 * A representation of protected resource request abstraction.
 */
public interface ProtectedResourceRequest {

    /**
     * Get the first occurrence of request's authorization header.
     * @return the authorization header value
     */
    String getAuthorizationHeader();

    /**
     * Get the native request.
     * @return the native request
     */
    <T> T getNativeRequest();

}

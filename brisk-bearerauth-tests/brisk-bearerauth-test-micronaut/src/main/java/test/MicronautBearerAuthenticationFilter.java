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

package test;

import java.io.IOException;

import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.OncePerRequestHttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import org.briskidentity.bearerauth.context.AuthorizationContextResolver;
import org.briskidentity.bearerauth.context.PropertiesAuthorizationContextResolver;
import org.briskidentity.bearerauth.http.ProtectedResourceRequest;
import org.briskidentity.bearerauth.http.WwwAuthenticateBuilder;
import org.briskidentity.bearerauth.token.BearerTokenExtractor;
import org.briskidentity.bearerauth.token.error.BearerTokenError;
import org.briskidentity.bearerauth.token.error.BearerTokenException;

@Filter("/**")
public class MicronautBearerAuthenticationFilter extends OncePerRequestHttpServerFilter {

	private final BearerTokenExtractor bearerTokenExtractor = BearerTokenExtractor.authorizationHeader();

	private final AuthorizationContextResolver authorizationContextResolver;

	public MicronautBearerAuthenticationFilter() throws IOException {
		this.authorizationContextResolver = new PropertiesAuthorizationContextResolver();
	}

	@Override
	public Publisher<MutableHttpResponse<?>> doFilterOnce(HttpRequest<?> request, ServerFilterChain chain) {
		return Flowable.defer(() -> Flowable.just(this.bearerTokenExtractor.extract(new MicronautProtectedResourceRequest(request))))
				.flatMap(bearerToken -> Flowable.fromFuture(authorizationContextResolver.resolve(bearerToken).toCompletableFuture()))
				.flatMap(authorizationContext -> {
					request.setAttribute(HttpAttributes.PRINCIPAL, authorizationContext);
					return chain.proceed(request);
				})
				.onErrorResumeNext(ex -> {
					if (ex instanceof BearerTokenException) {
						return handleBearerTokenError(((BearerTokenException) ex).getError());
					}
					if (ex.getCause() instanceof BearerTokenException) {
						return handleBearerTokenError(((BearerTokenException) ex.getCause()).getError());
					}
					return Flowable.error(ex);
				});
	}

	private static Flowable<MutableHttpResponse<?>> handleBearerTokenError(BearerTokenError bearerTokenError) {
		String wwwAuthenticate = WwwAuthenticateBuilder.from(bearerTokenError).build();
		MutableHttpResponse<Object> response = HttpResponse.status(HttpStatus.valueOf(bearerTokenError.getHttpStatus()));
		response.getHeaders().set(HttpHeaders.WWW_AUTHENTICATE, wwwAuthenticate);
		return Flowable.just(response);
	}

	private static class MicronautProtectedResourceRequest implements ProtectedResourceRequest {

		private final HttpRequest<?> httpRequest;

		private MicronautProtectedResourceRequest(HttpRequest<?> httpRequest) {
			this.httpRequest = httpRequest;
		}

		@Override
		public String getAuthorizationHeader() {
			return this.httpRequest.getHeaders().getAuthorization().orElse(null);
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T getNativeRequest() {
			return (T) this.httpRequest;
		}

	}

}

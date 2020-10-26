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

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import org.briskidentity.bearerauth.token.BearerToken;

/**
 *
 */
public class PropertiesAuthorizationContextResolver extends MapAuthorizationContextResolver {

	public PropertiesAuthorizationContextResolver(Properties properties) {
		super(parseProperties(properties));
	}

	private static Map<BearerToken, AuthorizationContext> parseProperties(Properties properties) {
		Objects.requireNonNull(properties, "properties must not be null");
		Map<BearerToken, AuthorizationContext> authorizationContexts = new HashMap<>();
		properties.forEach((key, value) -> {
			String[] authorizationContextParts = ((String) value).split(",");
			if (authorizationContextParts.length == 0) {
				throw new IllegalStateException("Invalid authorization context entry");
			}
			Instant expiry = Instant.parse(authorizationContextParts[0]); // TODO
			Set<String> scopeValues = new LinkedHashSet<>();
			if ((authorizationContextParts.length > 1) && !authorizationContextParts[1].isEmpty()) {
				scopeValues.addAll(Arrays.asList(authorizationContextParts[1].split(" ")));
			}
			Map<String, Object> attributes = new HashMap<>();
			if (authorizationContextParts.length > 2) {
				for (String attributeEntry : Arrays.asList(authorizationContextParts).subList(2, authorizationContextParts.length)) {
					if (!attributeEntry.contains("=")) {
						throw new IllegalStateException("Invalid authorization context entry");
					}
					String[] attributeParts = attributeEntry.split("=");
					attributes.put(attributeParts[0], attributeParts[1]);
				}
			}
			authorizationContexts.put(
					new BearerToken((String) key), new AuthorizationContext(scopeValues, attributes));
		});
		return authorizationContexts;
	}

	public PropertiesAuthorizationContextResolver() throws IOException {
		this(loadProperties());
	}

	private static Properties loadProperties() throws IOException {
		Properties properties = new Properties();
		properties.load(PropertiesAuthorizationContextResolver.class.getClassLoader()
				.getResourceAsStream("bearer-tokens.properties"));
		return properties;
	}

}

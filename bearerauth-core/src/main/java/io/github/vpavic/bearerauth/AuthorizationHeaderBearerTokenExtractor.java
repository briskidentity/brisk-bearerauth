package io.github.vpavic.bearerauth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link BearerTokenExtractor} implementation that extracts token from {@code Authorization} HTTP request header.
 */
class AuthorizationHeaderBearerTokenExtractor implements BearerTokenExtractor {

    private static final Pattern authorizationHeaderPattern = Pattern.compile("^Bearer (?<token>[A-Za-z0-9\\-._~+/]+=*)");

    @Override
    public BearerToken apply(HttpExchange httpExchange) {
        String authorizationHeader = httpExchange.getRequestHeader("Authorization");
        if (authorizationHeader == null) {
            return null;
        }
        Matcher matcher = authorizationHeaderPattern.matcher(authorizationHeader);
        if (!matcher.matches()) {
            return null;
        }
        return new BearerToken(matcher.group("token"));
    }

}

package org.briskidentity.bearerauth.http;

import org.briskidentity.bearerauth.token.error.BearerTokenError;
import org.briskidentity.bearerauth.token.error.BearerTokenException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Helper for building {@code WWW-Authenticate} header value.
 */
public final class WwwAuthenticateBuilder {

    private final BearerTokenException bearerTokenException;

    private String realm;

    private WwwAuthenticateBuilder(BearerTokenException bearerTokenException) {
        Objects.requireNonNull(bearerTokenException, "bearerTokenException must not be null");
        this.bearerTokenException = bearerTokenException;
    }

    /**
     * Create a builder instance for given bearer token exception.
     * @param bearerTokenException the bearer token exception
     * @return the builder instance
     */
    public static WwwAuthenticateBuilder from(BearerTokenException bearerTokenException) {
        return new WwwAuthenticateBuilder(bearerTokenException);
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
        BearerTokenError error = this.bearerTokenException.getError();
        if (error != null) {
            attributes.add(buildAttribute("error", error.getCode()));
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

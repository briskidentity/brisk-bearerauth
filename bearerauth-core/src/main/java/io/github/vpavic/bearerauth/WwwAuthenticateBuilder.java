package io.github.vpavic.bearerauth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class WwwAuthenticateBuilder {

    private final BearerTokenException bearerTokenException;

    private String realm;

    private WwwAuthenticateBuilder(BearerTokenException bearerTokenException) {
        Objects.requireNonNull(bearerTokenException, "bearerTokenException must not be null");
        this.bearerTokenException = bearerTokenException;
    }

    public static WwwAuthenticateBuilder from(BearerTokenException bearerTokenException) {
        return new WwwAuthenticateBuilder(bearerTokenException);
    }

    public WwwAuthenticateBuilder withRealm(String realm) {
        Objects.requireNonNull(realm, "realm must not be null");
        this.realm = realm;
        return this;
    }

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

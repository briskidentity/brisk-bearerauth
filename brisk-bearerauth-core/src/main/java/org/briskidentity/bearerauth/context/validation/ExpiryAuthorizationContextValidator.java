package org.briskidentity.bearerauth.context.validation;

import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.http.HttpExchange;
import org.briskidentity.bearerauth.token.error.BearerTokenError;
import org.briskidentity.bearerauth.token.error.BearerTokenException;

import java.time.Clock;
import java.util.Objects;

/**
 *
 */
class ExpiryAuthorizationContextValidator implements AuthorizationContextValidator {

    private final Clock clock;

    /**
     * @param clock
     */
    ExpiryAuthorizationContextValidator(Clock clock) {
        Objects.requireNonNull(clock, "clock must not be null");
        this.clock = clock;
    }

    /**
     *
     */
    ExpiryAuthorizationContextValidator() {
        this(Clock.systemUTC());
    }

    @Override
    public void accept(HttpExchange httpExchange, AuthorizationContext authorizationContext) {
        if (this.clock.instant().isAfter(authorizationContext.getExpiry())) {
            throw new BearerTokenException(BearerTokenError.INVALID_TOKEN);
        }
    }

}

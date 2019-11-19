package io.github.vpavic.bearerauth;

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
    public void accept(AuthorizationContext authorizationContext) {
        if (this.clock.instant().isAfter(authorizationContext.getExpiry())) {
            throw new RuntimeException("The access token expired");
        }
    }

}

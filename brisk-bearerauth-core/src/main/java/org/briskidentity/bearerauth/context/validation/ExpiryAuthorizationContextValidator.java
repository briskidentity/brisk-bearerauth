package org.briskidentity.bearerauth.context.validation;

import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.token.error.BearerTokenError;
import org.briskidentity.bearerauth.token.error.BearerTokenException;
import org.briskidentity.bearerauth.util.CompletableFutureHelper;

import java.time.Clock;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

class ExpiryAuthorizationContextValidator implements AuthorizationContextValidator {

    static final ExpiryAuthorizationContextValidator INSTANCE = new ExpiryAuthorizationContextValidator();

    private final Clock clock;

    ExpiryAuthorizationContextValidator(Clock clock) {
        Objects.requireNonNull(clock, "clock must not be null");
        this.clock = clock;
    }

    private ExpiryAuthorizationContextValidator() {
        this(Clock.systemUTC());
    }

    @Override
    public CompletionStage<Void> validate(AuthorizationContext authorizationContext) {
        if (this.clock.instant().isAfter(authorizationContext.getExpiry())) {
            return CompletableFutureHelper.failedFuture(new BearerTokenException(BearerTokenError.INVALID_TOKEN));
        }
        return CompletableFuture.completedFuture(null);
    }

}

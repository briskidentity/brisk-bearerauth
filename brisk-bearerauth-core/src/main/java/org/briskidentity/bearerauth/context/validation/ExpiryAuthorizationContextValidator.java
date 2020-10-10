package org.briskidentity.bearerauth.context.validation;

import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.token.error.BearerTokenError;
import org.briskidentity.bearerauth.token.error.BearerTokenException;

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
        CompletableFuture<Void> result = new CompletableFuture<>();
        if (this.clock.instant().isBefore(authorizationContext.getExpiry())) {
            result.complete(null);
        }
        else {
            result.completeExceptionally(new BearerTokenException(BearerTokenError.INVALID_TOKEN));
        }
        return result;
    }

}

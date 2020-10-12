package org.briskidentity.bearerauth;

import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.context.AuthorizationContextResolver;
import org.briskidentity.bearerauth.context.validation.AuthorizationContextValidator;
import org.briskidentity.bearerauth.http.ProtectedResourceRequest;
import org.briskidentity.bearerauth.token.BearerToken;
import org.briskidentity.bearerauth.token.BearerTokenExtractor;
import org.briskidentity.bearerauth.token.error.BearerTokenError;
import org.briskidentity.bearerauth.token.error.BearerTokenException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * Tests for {@link BearerAuthenticationHandler}.
 */
class BearerAuthenticationHandlerTests {

    private AutoCloseable mocks;

    @Mock
    private AuthorizationContextResolver authorizationContextResolver;

    @Mock
    private BearerTokenExtractor bearerTokenExtractor;

    @Mock
    private AuthorizationContextValidator authorizationContextValidator;

    @Mock
    private ProtectedResourceRequest protectedResourceRequest;

    private BearerAuthenticationHandler bearerAuthenticationHandler;

    @BeforeEach
    void setUp() {
        this.mocks = MockitoAnnotations.openMocks(this);
        this.bearerAuthenticationHandler = BearerAuthenticationHandler.builder(this.authorizationContextResolver)
                .bearerTokenExtractor(this.bearerTokenExtractor)
                .authorizationContextValidator(this.authorizationContextValidator).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        this.mocks.close();
    }

    @Test
    void builder_NullAuthorizationContextResolver_ShouldThrowException() {
        assertThatNullPointerException().isThrownBy(() -> BearerAuthenticationHandler.builder(null))
                .withMessage("authorizationContextResolver must not be null");
    }

    @Test
    void builder_NullBearerTokenExtractor_ShouldThrowException() {
        assertThatNullPointerException().isThrownBy(() -> BearerAuthenticationHandler
                .builder(this.authorizationContextResolver).bearerTokenExtractor(null))
                .withMessage("bearerTokenExtractor must not be null");
    }

    @Test
    void builder_NullAuthorizationContextValidator_ShouldThrowException() {
        assertThatNullPointerException().isThrownBy(() -> BearerAuthenticationHandler
                .builder(this.authorizationContextResolver).authorizationContextValidator(null))
                .withMessage("authorizationContextValidator must not be null");
    }

    @Test
    void handle_NoBearerToken_ShouldThrowException() {
        assertThatExceptionOfType(CompletionException.class)
                .isThrownBy(() -> this.bearerAuthenticationHandler.handle(this.protectedResourceRequest)
                        .toCompletableFuture().join()).havingRootCause().isInstanceOf(BearerTokenException.class)
                .withMessage(null);
    }

    @Test
    void handle_NoAuthorizationContext_ShouldThrowException() {
        BearerToken bearerToken = new BearerToken("secret");
        given(this.protectedResourceRequest.getAuthorizationHeader()).willReturn(bearerToken.toString());
        given(this.bearerTokenExtractor.extract(any())).willReturn(bearerToken);
        given(this.authorizationContextResolver.resolve(bearerToken)).willReturn(
                failedFuture(new BearerTokenException(BearerTokenError.INVALID_TOKEN)));
        assertThatExceptionOfType(CompletionException.class)
                .isThrownBy(() -> this.bearerAuthenticationHandler.handle(this.protectedResourceRequest)
                        .toCompletableFuture().join()).havingRootCause().isInstanceOf(BearerTokenException.class)
                .withMessage(BearerTokenError.INVALID_TOKEN.getCode());
    }

    @Test
    void handle_InvalidAuthorizationContext_ShouldThrowException() {
        BearerToken bearerToken = new BearerToken("secret");
        AuthorizationContext authorizationContext = new AuthorizationContext(Collections.emptySet(), Instant.now(),
                Collections.emptyMap());
        given(this.protectedResourceRequest.getAuthorizationHeader()).willReturn(bearerToken.toString());
        given(this.bearerTokenExtractor.extract(any())).willReturn(bearerToken);
        given(this.authorizationContextResolver.resolve(bearerToken))
                .willReturn(CompletableFuture.completedFuture(authorizationContext));
        given(this.authorizationContextValidator.validate(authorizationContext)).willReturn(
                failedFuture(new BearerTokenException(BearerTokenError.INVALID_TOKEN)));
        assertThatExceptionOfType(CompletionException.class)
                .isThrownBy(() -> this.bearerAuthenticationHandler.handle(this.protectedResourceRequest)
                        .toCompletableFuture().join()).havingRootCause().isInstanceOf(BearerTokenException.class)
                .withMessage(BearerTokenError.INVALID_TOKEN.getCode());
    }

    @Test
    void handle_ValidRequest_ShouldReturnAuthorizationContext() {
        BearerToken bearerToken = new BearerToken("secret");
        AuthorizationContext authorizationContext = new AuthorizationContext(Collections.emptySet(), Instant.now(),
                Collections.emptyMap());
        given(this.protectedResourceRequest.getAuthorizationHeader()).willReturn(bearerToken.toString());
        given(this.bearerTokenExtractor.extract(any())).willReturn(bearerToken);
        given(this.authorizationContextResolver.resolve(bearerToken))
                .willReturn(CompletableFuture.completedFuture(authorizationContext));
        given(this.authorizationContextValidator.validate(authorizationContext))
                .willReturn(CompletableFuture.completedFuture(null));
        assertThat(this.bearerAuthenticationHandler.handle(protectedResourceRequest).toCompletableFuture().join())
                .isEqualTo(authorizationContext);
    }

    private static <T> CompletableFuture<T> failedFuture(Exception ex) {
        CompletableFuture<T> result = new CompletableFuture<>();
        result.completeExceptionally(ex);
        return result;
    }

}

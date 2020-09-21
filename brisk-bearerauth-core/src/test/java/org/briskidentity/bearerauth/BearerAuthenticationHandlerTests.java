package org.briskidentity.bearerauth;

import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.context.AuthorizationContextResolver;
import org.briskidentity.bearerauth.context.validation.AuthorizationContextValidator;
import org.briskidentity.bearerauth.http.HttpExchange;
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

import static org.assertj.core.api.Assertions.assertThat;
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
    private HttpExchange httpExchange;

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
    void handle_NoBearerToken_ShouldCompleteExceptionally() {
        assertThat(this.bearerAuthenticationHandler.handle(this.httpExchange)).isCompletedExceptionally()
                .hasFailedWithThrowableThat().isInstanceOf(BearerTokenException.class)
                .hasFieldOrPropertyWithValue("error", null);
    }

    @Test
    void handle_NoAuthorizationContext_ShouldCompleteExceptionally() {
        BearerToken bearerToken = new BearerToken("secret");
        given(this.httpExchange.getRequestHeader("Authorization")).willReturn(bearerToken.toString());
        given(this.bearerTokenExtractor.extract(any())).willReturn(bearerToken);
        given(this.authorizationContextResolver.resolve(bearerToken)).willReturn(CompletableFuture.completedFuture(null));
        assertThat(this.bearerAuthenticationHandler.handle(this.httpExchange)).isCompletedExceptionally()
                .hasFailedWithThrowableThat().isInstanceOf(BearerTokenException.class)
                .hasFieldOrPropertyWithValue("error", BearerTokenError.INVALID_TOKEN);
    }

    @Test
    void handle_ValidRequest_ShouldComplete() {
        BearerToken bearerToken = new BearerToken("secret");
        AuthorizationContext authorizationContext = new AuthorizationContext(Collections.emptySet(), Instant.now(),
                Collections.emptyMap());
        given(this.httpExchange.getRequestHeader("Authorization")).willReturn(bearerToken.toString());
        given(this.bearerTokenExtractor.extract(any())).willReturn(bearerToken);
        given(this.authorizationContextResolver.resolve(bearerToken))
                .willReturn(CompletableFuture.completedFuture(authorizationContext));
        assertThat(this.bearerAuthenticationHandler.handle(httpExchange)).isCompletedWithValue(authorizationContext);
    }

}

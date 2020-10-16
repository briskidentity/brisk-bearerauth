package org.briskidentity.bearerauth.http;

import org.briskidentity.bearerauth.token.error.BearerTokenError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * Tests for {@link WwwAuthenticateBuilder}.
 */
class WwwAuthenticateBuilderTests {

    @Test
    void build_NullError_ShouldThrowNullPointerException() {
        assertThatNullPointerException().isThrownBy(() -> WwwAuthenticateBuilder.from(null).build())
                .withMessage("bearerTokenError must not be null");
    }

    @Test
    void build_BearerTokenErrorWithNoCode_ShouldReturnWwwAuthenticateWithNoError() {
        String wwwAuthenticate = WwwAuthenticateBuilder.from(BearerTokenError.NO_AUTHENTICATION).build();
        assertThat(wwwAuthenticate).isEqualTo("Bearer");
    }

    @Test
    void build_BearerTokenErrorWithCode_ShouldReturnWwwAuthenticateWithError() {
        String wwwAuthenticate = WwwAuthenticateBuilder.from(BearerTokenError.INVALID_TOKEN).build();
        assertThat(wwwAuthenticate).isEqualTo("Bearer error=\"invalid_token\"");
    }

    @Test
    void build_WithRealmAndBearerTokenErrorWithNoCode_ShouldReturnWwwAuthenticateWithRealmAndNoError() {
        String wwwAuthenticate = WwwAuthenticateBuilder.from(BearerTokenError.NO_AUTHENTICATION).withRealm("example")
                .build();
        assertThat(wwwAuthenticate).isEqualTo("Bearer realm=\"example\"");
    }

    @Test
    void build_WithRealmAndBearerTokenErrorWithCode_ShouldReturnWwwAuthenticateWithRealmAndError() {
        String wwwAuthenticate = WwwAuthenticateBuilder.from(BearerTokenError.INVALID_TOKEN).withRealm("example")
                .build();
        assertThat(wwwAuthenticate).isEqualTo("Bearer realm=\"example\", error=\"invalid_token\"");
    }

}

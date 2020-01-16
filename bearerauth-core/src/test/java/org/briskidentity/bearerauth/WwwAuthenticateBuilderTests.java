package org.briskidentity.bearerauth;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * Tests for {@link WwwAuthenticateBuilder}.
 */
class WwwAuthenticateBuilderTests {

    @Test
    void from_NullException_ShouldThrowException() {
        assertThatNullPointerException().isThrownBy(() -> WwwAuthenticateBuilder.from(null))
                .withMessage("bearerTokenException must not be null");
    }

    @Test
    void build_BearerTokenExceptionWithoutError_ShouldReturnWwwAuthenticate() {
        String wwwAuthenticate = WwwAuthenticateBuilder.from(new BearerTokenException()).build();
        assertThat(wwwAuthenticate).isEqualTo("Bearer");
    }

    @Test
    void build_BearerTokenExceptionWithError_ShouldReturnWwwAuthenticate() {
        String wwwAuthenticate = WwwAuthenticateBuilder.from(new BearerTokenException(BearerTokenError.INVALID_TOKEN))
                .build();
        assertThat(wwwAuthenticate).isEqualTo("Bearer error=\"invalid_token\"");
    }

    @Test
    void build_WithRealmBearerTokenExceptionWithoutError_ShouldReturnWwwAuthenticate() {
        String wwwAuthenticate = WwwAuthenticateBuilder.from(new BearerTokenException()).withRealm("example").build();
        assertThat(wwwAuthenticate).isEqualTo("Bearer realm=\"example\"");
    }

    @Test
    void build_WithRealmAndBearerTokenExceptionWithError_ShouldReturnWwwAuthenticate() {
        String wwwAuthenticate = WwwAuthenticateBuilder.from(new BearerTokenException(BearerTokenError.INVALID_TOKEN))
                .withRealm("example").build();
        assertThat(wwwAuthenticate).isEqualTo("Bearer realm=\"example\", error=\"invalid_token\"");
    }

}

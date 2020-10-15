package org.briskidentity.bearerauth.http;

import org.briskidentity.bearerauth.token.error.InsufficientScopeException;
import org.briskidentity.bearerauth.token.error.InvalidBearerTokenException;
import org.briskidentity.bearerauth.token.error.MissingBearerTokenException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * Tests for {@link WwwAuthenticateBuilder}.
 */
class WwwAuthenticateBuilderTests {

    @Test
    void from_NullException_ShouldThrowException() {
        assertThatNullPointerException().isThrownBy(() -> WwwAuthenticateBuilder.from(null).build())
                .withMessage("bearerTokenException must not be null");
    }

    @Test
    void build_MissingBearerTokenException_ShouldReturnWwwAuthenticateWithNoErrorCode() {
        String wwwAuthenticate = WwwAuthenticateBuilder.from(new MissingBearerTokenException()).build();
        assertThat(wwwAuthenticate).isEqualTo("Bearer");
    }

    @Test
    void build_InvalidBearerTokenException_ShouldReturnWwwAuthenticateWithErrorCode() {
        String wwwAuthenticate = WwwAuthenticateBuilder.from(new InvalidBearerTokenException()).build();
        assertThat(wwwAuthenticate).isEqualTo("Bearer error=\"invalid_token\"");
    }

    @Test
    void build_InsufficientScopeException_ShouldReturnWwwAuthenticateWithErrorCode() {
        String wwwAuthenticate = WwwAuthenticateBuilder.from(new InsufficientScopeException()).build();
        assertThat(wwwAuthenticate).isEqualTo("Bearer error=\"insufficient_scope\"");
    }

    @Test
    void build_RealmConfiguredAndMissingBearerTokenException_ShouldReturnWwwAuthenticateWithRealmAndNoErrorCode() {
        String wwwAuthenticate = WwwAuthenticateBuilder.from(new MissingBearerTokenException()).withRealm("example")
                .build();
        assertThat(wwwAuthenticate).isEqualTo("Bearer realm=\"example\"");
    }

    @Test
    void build_RealmConfiguredAndInvalidBearerTokenException_ShouldReturnWwwAuthenticateWithRealmAndErrorCode() {
        String wwwAuthenticate = WwwAuthenticateBuilder.from(new InvalidBearerTokenException()).withRealm("example")
                .build();
        assertThat(wwwAuthenticate).isEqualTo("Bearer realm=\"example\", error=\"invalid_token\"");
    }

    @Test
    void build_RealmConfiguredAndInsufficientScopeException_ShouldReturnWwwAuthenticateWithRealmAndErrorCode() {
        String wwwAuthenticate = WwwAuthenticateBuilder.from(new InsufficientScopeException()).withRealm("example")
                .build();
        assertThat(wwwAuthenticate).isEqualTo("Bearer realm=\"example\", error=\"insufficient_scope\"");
    }

}

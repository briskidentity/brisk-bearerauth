package org.briskidentity.bearerauth.token;

import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * Tests for {@link BearerToken}.
 */
class BearerTokenTests {

    @Test
    void constructor_NullValue_ShouldThrowException() {
        assertThatNullPointerException().isThrownBy(() -> new BearerToken(null)).withMessage("value must not be null");
    }

    @Test
    void isSerializable_ValidValue_ShouldReturnTrue() {
        assertThat(new BearerToken("test") instanceof Serializable).isTrue();
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void equals_SameToken_ShouldReturnTrue() {
        BearerToken bearerToken = new BearerToken("test");
        assertThat(bearerToken.equals(bearerToken)).isTrue();
    }

    @Test
    void equals_TokenWithSameValue_ShouldReturnTrue() {
        assertThat(new BearerToken("test").equals(new BearerToken("test"))).isTrue();
    }

    @Test
    void equals_TokenWithDifferentValue_ShouldReturnFalse() {
        assertThat(new BearerToken("test").equals(new BearerToken("abcd"))).isFalse();
    }

    @Test
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    void equals_StringWithSameValue_ShouldReturnFalse() {
        assertThat(new BearerToken("test").equals("test")).isFalse();
    }

}

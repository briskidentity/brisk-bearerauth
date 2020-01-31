package org.briskidentity.bearerauth.token;

import org.briskidentity.bearerauth.http.HttpExchange;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * Tests for {@link AuthorizationHeaderBearerTokenExtractor}.
 */
class AuthorizationHeaderBearerTokenExtractorTests {

    private BearerTokenExtractor bearerTokenExtractor = new AuthorizationHeaderBearerTokenExtractor();

    @Test
    void apply_NullHttpExchange_ShouldThrowException() {
        assertThatNullPointerException().isThrownBy(() -> this.bearerTokenExtractor.apply(null))
                .withMessage("httpExchange must not be null");
    }

    @Test
    void apply_HttpExchangeWithNoAuthorizationHeader_ShouldReturnNull() {
        assertThat(this.bearerTokenExtractor.apply(new TestHttpExchange(null))).isNull();
    }

    @Test
    void apply_HttpExchangeWithUnsupportedAuthorizationHeaderScheme_ShouldReturnNull() {
        assertThat(this.bearerTokenExtractor.apply(new TestHttpExchange("Basic test"))).isNull();
    }

    @Test
    void apply_HttpExchangeWithAuthorizationHeader_ShouldReturnNull() {
        assertThat(this.bearerTokenExtractor.apply(new TestHttpExchange("bearer test"))).isNull();
    }

    @Test
    void apply_HttpExchangeWithValidAuthorizationHeader_ShouldReturnToken() {
        assertThat(this.bearerTokenExtractor.apply(new TestHttpExchange("Bearer test")))
                .isEqualTo(new BearerToken("test"));
    }

    private static class TestHttpExchange implements HttpExchange {

        private final String authorizationHeaderValue;

        private TestHttpExchange(String authorizationHeaderValue) {
            this.authorizationHeaderValue = authorizationHeaderValue;
        }

        @Override
        public String getRequestMethod() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getRequestPath() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getRequestHeader(String headerName) {
            return "Authorization".equals(headerName) ? this.authorizationHeaderValue : null;
        }

        @Override
        public void setAttribute(String attributeName, Object attributeValue) {
            throw new UnsupportedOperationException();
        }

    }

}

package org.briskidentity.bearerauth.token;

import org.briskidentity.bearerauth.http.HttpExchange;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * Tests for {@link AuthorizationHeaderBearerTokenExtractor}.
 */
class AuthorizationHeaderBearerTokenExtractorTests {

    private final BearerTokenExtractor bearerTokenExtractor = AuthorizationHeaderBearerTokenExtractor.INSTANCE;

    @Test
    void extract_NullHttpExchange_ShouldThrowException() {
        assertThatNullPointerException().isThrownBy(() -> this.bearerTokenExtractor.extract(null))
                .withMessage("httpExchange must not be null");
    }

    @Test
    void extract_HttpExchangeWithNoAuthorizationHeader_ShouldReturnNull() {
        assertThat(this.bearerTokenExtractor.extract(new TestHttpExchange(null))).isNull();
    }

    @Test
    void extract_HttpExchangeWithUnsupportedAuthorizationHeaderScheme_ShouldReturnNull() {
        assertThat(this.bearerTokenExtractor.extract(new TestHttpExchange("Basic test"))).isNull();
    }

    @Test
    void extract_HttpExchangeWithAuthorizationHeader_ShouldReturnNull() {
        assertThat(this.bearerTokenExtractor.extract(new TestHttpExchange("bearer test"))).isNull();
    }

    @Test
    void extract_HttpExchangeWithValidAuthorizationHeader_ShouldReturnToken() {
        assertThat(this.bearerTokenExtractor.extract(new TestHttpExchange("Bearer test")))
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

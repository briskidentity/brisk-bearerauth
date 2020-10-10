package org.briskidentity.bearerauth.token;

import org.briskidentity.bearerauth.http.ProtectedResourceRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * Tests for {@link AuthorizationHeaderBearerTokenExtractor}.
 */
class AuthorizationHeaderBearerTokenExtractorTests {

    private final BearerTokenExtractor bearerTokenExtractor = AuthorizationHeaderBearerTokenExtractor.INSTANCE;

    @Test
    void extract_NullRequest_ShouldThrowException() {
        assertThatNullPointerException().isThrownBy(() -> this.bearerTokenExtractor.extract(null))
                .withMessage("request must not be null");
    }

    @Test
    void extract_RequestWithNoAuthorizationHeader_ShouldReturnNull() {
        assertThat(this.bearerTokenExtractor.extract(new TestProtectedResourceRequest(null))).isNull();
    }

    @Test
    void extract_RequestWithUnsupportedAuthorizationHeaderScheme_ShouldReturnNull() {
        assertThat(this.bearerTokenExtractor.extract(new TestProtectedResourceRequest("Basic test"))).isNull();
    }

    @Test
    void extract_RequestWithAuthorizationHeader_ShouldReturnNull() {
        assertThat(this.bearerTokenExtractor.extract(new TestProtectedResourceRequest("bearer test"))).isNull();
    }

    @Test
    void extract_RequestWithValidAuthorizationHeader_ShouldReturnToken() {
        assertThat(this.bearerTokenExtractor.extract(new TestProtectedResourceRequest("Bearer test")))
                .isEqualTo(new BearerToken("test"));
    }

    private static class TestProtectedResourceRequest implements ProtectedResourceRequest {

        private final String authorizationHeaderValue;

        private TestProtectedResourceRequest(String authorizationHeaderValue) {
            this.authorizationHeaderValue = authorizationHeaderValue;
        }

        @Override
        public String getAuthorizationHeader() {
            return this.authorizationHeaderValue;
        }

        @Override
        public <T> T getNativeRequest() {
            throw new UnsupportedOperationException();
        }

    }

}

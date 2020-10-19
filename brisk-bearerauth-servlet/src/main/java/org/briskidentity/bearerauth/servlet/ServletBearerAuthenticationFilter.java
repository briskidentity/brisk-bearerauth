package org.briskidentity.bearerauth.servlet;

import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.context.AuthorizationContextResolver;
import org.briskidentity.bearerauth.http.ProtectedResourceRequest;
import org.briskidentity.bearerauth.http.WwwAuthenticateBuilder;
import org.briskidentity.bearerauth.token.BearerToken;
import org.briskidentity.bearerauth.token.BearerTokenExtractor;
import org.briskidentity.bearerauth.token.error.BearerTokenError;
import org.briskidentity.bearerauth.token.error.BearerTokenException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;

public class ServletBearerAuthenticationFilter implements Filter {

    private final BearerTokenExtractor bearerTokenExtractor;

    private final AuthorizationContextResolver authorizationContextResolver;

    public ServletBearerAuthenticationFilter(BearerTokenExtractor bearerTokenExtractor,
            AuthorizationContextResolver authorizationContextResolver) {
        Objects.requireNonNull(bearerTokenExtractor, "bearerTokenExtractor must not be null");
        Objects.requireNonNull(authorizationContextResolver, "authorizationContextResolver must not be null");
        this.bearerTokenExtractor = bearerTokenExtractor;
        this.authorizationContextResolver = authorizationContextResolver;
    }

    public ServletBearerAuthenticationFilter(AuthorizationContextResolver authorizationContextResolver) {
        this(BearerTokenExtractor.authorizationHeader(), authorizationContextResolver);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException("ServletBearerAuthenticationFilter only supports HTTP requests");
        }
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        try {
            BearerToken bearerToken = this.bearerTokenExtractor.extract(new ServletProtectedResourceRequest(req));
            AuthorizationContext authorizationContext = this.authorizationContextResolver.resolve(bearerToken)
                    .toCompletableFuture().join();
            chain.doFilter(new AuthorizedRequest(req, authorizationContext), response);
        }
        catch (BearerTokenException ex) {
            handleBearerTokenError(ex.getError(), res);
        }
        catch (CompletionException ex) {
            Throwable cause = ex.getCause();
            if (!(cause instanceof BearerTokenException)) {
                throw new ServletException(cause);
            }
            handleBearerTokenError(((BearerTokenException) cause).getError(), res);
        }
        catch (CancellationException ex) {
            throw new ServletException(ex);
        }
    }

    private static void handleBearerTokenError(BearerTokenError bearerTokenError, HttpServletResponse response)
            throws IOException {
        String wwwAuthenticate = WwwAuthenticateBuilder.from(bearerTokenError).build();
        response.addHeader("WWW-Authenticate", wwwAuthenticate);
        response.sendError(bearerTokenError.getHttpStatus());
    }

    @Override
    public void destroy() {
    }

    private static class ServletProtectedResourceRequest implements ProtectedResourceRequest {

        private final HttpServletRequest httpServletRequest;

        private ServletProtectedResourceRequest(HttpServletRequest httpServletRequest) {
            this.httpServletRequest = httpServletRequest;
        }

        @Override
        public String getAuthorizationHeader() {
            return this.httpServletRequest.getHeader("Authorization");
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getNativeRequest() {
            return (T) this.httpServletRequest;
        }

    }

    private static class AuthorizedRequest extends HttpServletRequestWrapper {

        private final AuthorizationContext authorizationContext;

        private AuthorizedRequest(HttpServletRequest request, AuthorizationContext authorizationContext) {
            super(request);
            this.authorizationContext = authorizationContext;
        }

        @Override
        public String getAuthType() {
            return "BEARER";
        }

        @Override
        public String getRemoteUser() {
            return this.authorizationContext.getName();
        }

        @Override
        public boolean isUserInRole(String role) {
            if ("*".equals(role)) {
                return false;
            }
            if ("**".equals(role)) {
                return true;
            }
            return this.authorizationContext.getScopeValues().contains(role);
        }

        @Override
        public Principal getUserPrincipal() {
            return this.authorizationContext;
        }

    }

}

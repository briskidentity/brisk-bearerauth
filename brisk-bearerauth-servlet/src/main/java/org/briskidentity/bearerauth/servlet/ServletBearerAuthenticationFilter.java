package org.briskidentity.bearerauth.servlet;

import org.briskidentity.bearerauth.BearerAuthenticationHandler;
import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.http.ProtectedResourceRequest;
import org.briskidentity.bearerauth.http.WwwAuthenticateBuilder;
import org.briskidentity.bearerauth.token.error.BearerTokenException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class ServletBearerAuthenticationFilter implements Filter {

    private final BearerAuthenticationHandler bearerAuthenticationHandler;

    public ServletBearerAuthenticationFilter(BearerAuthenticationHandler bearerAuthenticationHandler) {
        Objects.requireNonNull(bearerAuthenticationHandler, "bearerAuthenticationHandler must not be null");
        this.bearerAuthenticationHandler = bearerAuthenticationHandler;
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
            AuthorizationContext authorizationContext = this.bearerAuthenticationHandler.handle(
                    new ServletProtectedResourceRequest(req)).toCompletableFuture().get();
            chain.doFilter(new AuthorizedRequest(req, authorizationContext), response);
        }
        catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (!(cause instanceof BearerTokenException)) {
                throw new ServletException(ex);
            }
            BearerTokenException bearerTokenException = (BearerTokenException) cause;
            String wwwAuthenticate = WwwAuthenticateBuilder.from(bearerTokenException).build();
            res.addHeader("WWW-Authenticate", wwwAuthenticate);
            res.sendError(bearerTokenException.getStatus());
        }
        catch (InterruptedException ex) {
            throw new ServletException(ex);
        }
    }

    private static class ServletProtectedResourceRequest implements ProtectedResourceRequest {

        private final HttpServletRequest httpServletRequest;

        private ServletProtectedResourceRequest(HttpServletRequest httpServletRequest) {
            this.httpServletRequest = httpServletRequest;
        }

        @Override
        public String getRequestMethod() {
            return this.httpServletRequest.getMethod();
        }

        @Override
        public String getRequestPath() {
            return this.httpServletRequest.getRequestURI();
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
            return this.authorizationContext.getScopeValues().contains(role);
        }

        @Override
        public Principal getUserPrincipal() {
            return this.authorizationContext;
        }

    }

}

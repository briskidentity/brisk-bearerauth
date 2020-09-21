package org.briskidentity.bearerauth.servlet;

import org.briskidentity.bearerauth.BearerAuthenticationHandler;
import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.http.HttpExchange;
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
            this.bearerAuthenticationHandler.handle(new ServletHttpExchange(req)).toCompletableFuture().get();
            chain.doFilter(new AuthorizedRequest(req), response);
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

    private static class ServletHttpExchange implements HttpExchange {

        private final HttpServletRequest httpServletRequest;

        private ServletHttpExchange(HttpServletRequest httpServletRequest) {
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
        public String getRequestHeader(String headerName) {
            return this.httpServletRequest.getHeader(headerName);
        }

        @Override
        public void setAttribute(String attributeName, Object attributeValue) {
            httpServletRequest.setAttribute(attributeName, attributeValue);
        }

    }

    private static class AuthorizedRequest extends HttpServletRequestWrapper {

        private AuthorizedRequest(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getAuthType() {
            return "BEARER";
        }

        @Override
        public String getRemoteUser() {
            AuthorizationContext authorizationContext = getAuthorizationContext();
            if (authorizationContext == null) {
                return null;
            }
            return authorizationContext.getName();
        }

        @Override
        public boolean isUserInRole(String role) {
            AuthorizationContext authorizationContext = getAuthorizationContext();
            if (authorizationContext == null) {
                return false;
            }
            return authorizationContext.getScopeValues().contains(role);
        }

        @Override
        public Principal getUserPrincipal() {
            return getAuthorizationContext();
        }

        private AuthorizationContext getAuthorizationContext() {
            Object authorizationContext = getAttribute(BearerAuthenticationHandler.AUTHORIZATION_CONTEXT_ATTRIBUTE);
            if ((authorizationContext instanceof AuthorizationContext)) {
                return (AuthorizationContext) authorizationContext;
            }
            return null;
        }

    }

}

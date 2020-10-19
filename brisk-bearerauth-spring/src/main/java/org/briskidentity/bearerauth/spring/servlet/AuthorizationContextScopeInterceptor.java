package org.briskidentity.bearerauth.spring.servlet;

import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.context.validation.AuthorizationContextValidator;
import org.briskidentity.bearerauth.http.WwwAuthenticateBuilder;
import org.briskidentity.bearerauth.spring.RequiresScope;
import org.briskidentity.bearerauth.token.error.BearerTokenError;
import org.briskidentity.bearerauth.token.error.BearerTokenException;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletionException;

public class AuthorizationContextScopeInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequiresScope requiresScope = handlerMethod.getMethodAnnotation(RequiresScope.class);
        if (requiresScope == null) {
            return true;
        }
        if (!(request.getUserPrincipal() instanceof AuthorizationContext)) {
            throw new IllegalStateException("Authorization context cannot be resolved");
        }
        AuthorizationContext authorizationContext = (AuthorizationContext) request.getUserPrincipal();
        try {
            AuthorizationContextValidator.scope(Arrays.asList(requiresScope.value())).validate(authorizationContext)
                    .toCompletableFuture().join();
        }
        catch (CompletionException ex) {
            Throwable cause = ex.getCause();
            if (!(cause instanceof BearerTokenException)) {
                throw ex;
            }
            handleBearerTokenError(((BearerTokenException) cause).getError(), response);
            return false;
        }
        return true;
    }

    private static void handleBearerTokenError(BearerTokenError bearerTokenError, HttpServletResponse response)
            throws IOException {
        String wwwAuthenticate = WwwAuthenticateBuilder.from(bearerTokenError).build();
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, wwwAuthenticate);
        response.sendError(bearerTokenError.getHttpStatus());
    }

}

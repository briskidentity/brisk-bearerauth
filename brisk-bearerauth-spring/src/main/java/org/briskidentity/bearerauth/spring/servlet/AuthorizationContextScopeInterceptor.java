package org.briskidentity.bearerauth.spring.servlet;

import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.http.WwwAuthenticateBuilder;
import org.briskidentity.bearerauth.spring.RequiresScope;
import org.briskidentity.bearerauth.token.error.InsufficientScopeException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class AuthorizationContextScopeInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequiresScope requiresScope = handlerMethod.getMethodAnnotation(RequiresScope.class);
        if (requiresScope == null) {
            return true;
        }
        if (request.getUserPrincipal() instanceof AuthorizationContext) {
            AuthorizationContext authorizationContext = (AuthorizationContext) request.getUserPrincipal();
            if (authorizationContext.getScopeValues().containsAll(Arrays.asList(requiresScope.value()))) {
                return true;
            }
        }
        InsufficientScopeException ex = new InsufficientScopeException();
        String wwwAuthenticate = WwwAuthenticateBuilder.from(ex).build();
        response.addHeader("WWW-Authenticate", wwwAuthenticate);
        response.sendError(ex.getHttpStatus());
        return false;
    }

}

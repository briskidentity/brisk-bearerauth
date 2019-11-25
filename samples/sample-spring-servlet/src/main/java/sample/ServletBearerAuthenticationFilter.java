package sample;

import io.github.vpavic.bearerauth.AuthorizationContext;
import io.github.vpavic.bearerauth.AuthorizationContextResolver;
import io.github.vpavic.bearerauth.BearerAuthenticationHandler;
import io.github.vpavic.bearerauth.HttpExchange;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@WebFilter(urlPatterns = "/resource")
public class ServletBearerAuthenticationFilter extends HttpFilter {

    private final BearerAuthenticationHandler bearerAuthenticationHandler;

    public ServletBearerAuthenticationFilter() {
        this.bearerAuthenticationHandler = BearerAuthenticationHandler.builder(
                bearerToken -> authorizationContextResolver().apply(bearerToken)).build();
    }

    private AuthorizationContextResolver authorizationContextResolver() {
        return bearerToken -> {
            switch (bearerToken.toString()) {
                case "valid":
                    return CompletableFuture.completedFuture(
                            new AuthorizationContext(Collections.emptySet(), Instant.MAX, Collections.emptyMap()));
                case "expired":
                    return CompletableFuture.completedFuture(
                            new AuthorizationContext(Collections.emptySet(), Instant.MIN, Collections.emptyMap()));
                default:
                    return CompletableFuture.completedFuture(null);
            }
        };
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            this.bearerAuthenticationHandler.handle(new ServletHttpExchange(request)).toCompletableFuture().get();
        }
        catch (ExecutionException | InterruptedException ex) {
            throw new ServletException(ex);
        }
        chain.doFilter(request, response);
    }

    private static class ServletHttpExchange implements HttpExchange {

        private final HttpServletRequest httpServletRequest;

        private ServletHttpExchange(HttpServletRequest httpServletRequest) {
            this.httpServletRequest = httpServletRequest;
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

}

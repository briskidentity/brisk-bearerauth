package sample;

import org.briskidentity.bearerauth.BearerAuthenticationHandler;
import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.context.AuthorizationContextResolver;
import org.briskidentity.bearerauth.context.MapAuthorizationContextResolver;
import org.briskidentity.bearerauth.servlet.ServletBearerAuthenticationFilter;
import org.briskidentity.bearerauth.token.BearerToken;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class ServletTestApplication {

    private static final String SERVLET_PATH = "/resource";

    public static void main(String[] args) throws Exception {
        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");
        handler.addServlet(TestServlet.class, SERVLET_PATH);
        handler.addFilter(new FilterHolder(bearerAuthenticationFilter()), SERVLET_PATH,
                EnumSet.of(DispatcherType.REQUEST));
        Server server = new Server(8080);
        server.setHandler(handler);
        server.start();
    }

    private static ServletBearerAuthenticationFilter bearerAuthenticationFilter() {
        Map<BearerToken, AuthorizationContext> authorizationContexts = new HashMap<>();
        authorizationContexts.put(new BearerToken("valid_token"),
                new AuthorizationContext(Collections.singleton("scope:read"), Instant.MAX, Collections.emptyMap()));
        authorizationContexts.put(new BearerToken("token_expired"),
                new AuthorizationContext(Collections.singleton("scope:read"), Instant.MIN, Collections.emptyMap()));
        authorizationContexts.put(new BearerToken("insufficient_scope"),
                new AuthorizationContext(Collections.emptySet(), Instant.MAX, Collections.emptyMap()));
        AuthorizationContextResolver authorizationContextResolver =
                new MapAuthorizationContextResolver(authorizationContexts);
        BearerAuthenticationHandler bearerAuthenticationHandler = BearerAuthenticationHandler.builder(
                authorizationContextResolver).build();
        return new ServletBearerAuthenticationFilter(bearerAuthenticationHandler);
    }

    public static class TestServlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            AuthorizationContext authorizationContext = (AuthorizationContext) req.getUserPrincipal();
            System.out.println("authorizationContext{scope=" + String.join(",", authorizationContext.getScopeValues())
                    + ",expiry=" + authorizationContext.getExpiry() + "}");
            byte[] content = "Hello World!".getBytes(StandardCharsets.UTF_8);
            resp.setContentType("text/plain");
            resp.getOutputStream().write(content);
        }

    }

}

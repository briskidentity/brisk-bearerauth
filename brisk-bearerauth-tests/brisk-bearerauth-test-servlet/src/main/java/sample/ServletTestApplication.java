package sample;

import org.briskidentity.bearerauth.BearerAuthenticationHandler;
import org.briskidentity.bearerauth.context.PropertiesAuthorizationContextResolver;
import org.briskidentity.bearerauth.servlet.ServletBearerAuthenticationFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.logging.Logger;

public class ServletTestApplication {

    private static final String SERVLET_PATH = "/resource";

    private static final Logger logger = Logger.getLogger(ServletTestApplication.class.getName());

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

    private static ServletBearerAuthenticationFilter bearerAuthenticationFilter() throws IOException {
        BearerAuthenticationHandler bearerAuthenticationHandler = BearerAuthenticationHandler.builder(
                new PropertiesAuthorizationContextResolver()).build();
        return new ServletBearerAuthenticationFilter(bearerAuthenticationHandler);
    }

    public static class TestServlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            logger.info("Principal: " + req.getUserPrincipal());
            resp.setContentType("text/plain");
            resp.getOutputStream().write("Hello World!".getBytes(StandardCharsets.UTF_8));
        }

    }

}

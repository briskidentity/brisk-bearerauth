package sample;

import org.briskidentity.bearerauth.AuthorizationContext;
import org.briskidentity.bearerauth.BearerAuthenticationHandler;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet(urlPatterns = "/resource")
public class TestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AuthorizationContext authorizationContext = (AuthorizationContext) req.getAttribute(
                BearerAuthenticationHandler.AUTHORIZATION_CONTEXT_ATTRIBUTE);
        if (authorizationContext != null) {
            System.out.println("authorizationContext{scope=" + String.join(",", authorizationContext.getScope())
                    + ",expiry=" + authorizationContext.getExpiry() + "}");
        }
        byte[] content = "Hello World!".getBytes(StandardCharsets.UTF_8);
        resp.setContentType("text/plain");
        resp.getOutputStream().write(content);
    }

}
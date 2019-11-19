package sample;

import io.github.vpavic.bearerauth.AuthorizationContext;
import io.github.vpavic.bearerauth.BearerAuthenticationHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

@RestController
@RequestMapping(path = "/resource")
public class TestController {

    @GetMapping
    public String greet(ServerWebExchange exchange) {
        AuthorizationContext authorizationContext = exchange.getAttribute(
                BearerAuthenticationHandler.AUTHORIZATION_CONTEXT_ATTRIBUTE);
        if (authorizationContext != null) {
            System.out.println("authorizationContext{scope=" + String.join(",", authorizationContext.getScope())
                    + ",expiry=" + authorizationContext.getExpiry() + "}");
        }
        return "Hello World!";
    }

}

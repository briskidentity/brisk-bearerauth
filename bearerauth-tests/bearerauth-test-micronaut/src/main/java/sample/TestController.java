package sample;

import io.github.vpavic.bearerauth.AuthorizationContext;
import io.github.vpavic.bearerauth.BearerAuthenticationHandler;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/resource")
public class TestController {

    @Get(produces = MediaType.TEXT_PLAIN)
    public String greet(HttpRequest<?> request) {
        request.getAttribute(BearerAuthenticationHandler.AUTHORIZATION_CONTEXT_ATTRIBUTE, AuthorizationContext.class)
                .ifPresent(authorizationContext -> System.out.println("authorizationContext{scope="
                        + String.join(",", authorizationContext.getScope()) + ",expiry="
                        + authorizationContext.getExpiry() + "}"));
        return "Hello World!";
    }

}

package test;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import org.briskidentity.bearerauth.context.AuthorizationContext;

@Controller("/resource")
public class TestController {

    @Get(produces = MediaType.TEXT_PLAIN)
    public String greet(HttpRequest<?> request) {
        request.getUserPrincipal().ifPresent(principal -> {
            AuthorizationContext authorizationContext = (AuthorizationContext) principal;
            System.out.println("authorizationContext{scope=" + String.join(",", authorizationContext.getScopeValues())
                    + ",expiry=" + authorizationContext.getExpiry() + "}");
        });
        return "Hello World!";
    }

}

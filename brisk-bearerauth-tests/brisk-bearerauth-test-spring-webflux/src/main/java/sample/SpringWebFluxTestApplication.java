package sample;

import org.briskidentity.bearerauth.BearerAuthenticationHandler;
import org.briskidentity.bearerauth.context.AuthorizationContext;
import org.briskidentity.bearerauth.context.PropertiesAuthorizationContextResolver;
import org.briskidentity.bearerauth.spring.webflux.WebFluxBearerAuthenticationFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import java.io.IOException;

@SpringBootApplication
@RestController
public class SpringWebFluxTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringWebFluxTestApplication.class, args);
    }

    @GetMapping(path = "/resource")
    public String greet(ServerWebExchange exchange) {
        exchange.getPrincipal().subscribe(principal -> {
            AuthorizationContext authorizationContext = (AuthorizationContext) principal;
            System.out.println("authorizationContext{scope=" + String.join(",", authorizationContext.getScopeValues())
                    + ",expiry=" + authorizationContext.getExpiry() + "}");
        });
        return "Hello World!";
    }

    @Bean
    public WebFluxBearerAuthenticationFilter bearerAuthenticationFilter() throws IOException {
        BearerAuthenticationHandler bearerAuthenticationHandler = BearerAuthenticationHandler.builder(
                new PropertiesAuthorizationContextResolver()).build();
        return new WebFluxBearerAuthenticationFilter(bearerAuthenticationHandler);
    }

}

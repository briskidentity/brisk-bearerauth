package sample;

import org.briskidentity.bearerauth.BearerAuthenticationHandler;
import org.briskidentity.bearerauth.context.PropertiesAuthorizationContextResolver;
import org.briskidentity.bearerauth.spring.webflux.WebFluxBearerAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(SpringWebFluxTestApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringWebFluxTestApplication.class, args);
    }

    @GetMapping(path = "/resource")
    public String greet(ServerWebExchange exchange) {
        exchange.getPrincipal().subscribe(principal -> logger.info("Principal: {}", principal));
        return "Hello World!";
    }

    @Bean
    public WebFluxBearerAuthenticationFilter bearerAuthenticationFilter() throws IOException {
        BearerAuthenticationHandler bearerAuthenticationHandler = BearerAuthenticationHandler.builder(
                new PropertiesAuthorizationContextResolver()).build();
        return new WebFluxBearerAuthenticationFilter(bearerAuthenticationHandler);
    }

}

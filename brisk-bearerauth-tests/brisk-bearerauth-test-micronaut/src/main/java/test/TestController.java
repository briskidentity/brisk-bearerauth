package test;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/resource")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Get(produces = MediaType.TEXT_PLAIN)
    public String greet(HttpRequest<?> request) {
        logger.info("Principal: {}", request.getUserPrincipal());
        return "Hello World!";
    }

}

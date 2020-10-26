/*
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample;

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
        return new WebFluxBearerAuthenticationFilter(new PropertiesAuthorizationContextResolver());
    }

}

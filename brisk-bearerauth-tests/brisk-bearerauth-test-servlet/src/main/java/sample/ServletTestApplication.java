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
import org.briskidentity.bearerauth.servlet.ServletBearerAuthenticationFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

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
        handler.addServlet(new ServletHolder(new TestServlet()), SERVLET_PATH);
        handler.addFilter(new FilterHolder(new ServletBearerAuthenticationFilter(
                new PropertiesAuthorizationContextResolver())), SERVLET_PATH,
                EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR));
        Server server = new Server(8080);
        server.setHandler(handler);
        server.start();
    }

    private static class TestServlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            logger.info("Principal: " + req.getUserPrincipal());
            resp.setContentType("text/plain");
            resp.getOutputStream().write("Hello World!".getBytes(StandardCharsets.UTF_8));
        }

    }

}

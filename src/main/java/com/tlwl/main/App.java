package com.tlwl.main;

import com.tlwl.filter.RequestFilter;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class App {
    public static final String BASE_URI = "http://0.0.0.0:"+GlobalConst.CONFIGS.getInt("ServPort")+"/";

    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example package
        final ResourceConfig rc = new ResourceConfig().register(RequestFilter.class);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started at %s...", BASE_URI));
        //System.in.read();
        //server.stop();
    }

}
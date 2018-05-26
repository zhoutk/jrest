package com.tlwl.main;

import java.net.URI;

import org.eclipse.jetty.server.Server;//jetty包
import javax.ws.rs.core.UriBuilder;//jersey-container-jetty-http
import org.glassfish.jersey.server.ResourceConfig;//
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;//jersey-container-jetty-http  + jersey-hk2
import com.tlwl.router.*;

public class App {

    public static void main(String[] args) throws Exception {

        URI baseUri = UriBuilder.fromUri("http://localhost/").port(5000).build();
        ResourceConfig config = new ResourceConfig(Echo.class, Rs.class, Op.class);//jersey-server
        Server server = JettyHttpContainerFactory.createServer(baseUri, config);//pom
        System.out.println("JettyServer Running on 127.0.0.1:5000");
        server.start();

    }
}
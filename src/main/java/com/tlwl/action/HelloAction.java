package com.tlwl.action;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class HelloAction {

    @GET
    @Path("{name}")
    @Produces(MediaType.TEXT_PLAIN)
    //访问路径 /hello/everyone
    public String hello(@PathParam("name") String name){
        System.out.println(name);
        return "hello wolrd! "+name;
    }
}
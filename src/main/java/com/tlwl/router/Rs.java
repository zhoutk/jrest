package com.tlwl.router;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/rs")
public class Rs {

    @GET
    @Path("{name}")
    @Produces(MediaType.TEXT_PLAIN)
    //访问路径 /echo/everyone
    public String hello(@PathParam("name") String name){
        System.out.println("rs say: " + name);
        return "rs say: " + name;
    }
}


package com.tlwl.router;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("{ps:(.*)}")
public class Zrs {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String zrs(){
        System.out.println("rest api not exist. ");
        return "{\"code\": 404, \"err\": \"the rest api not exist.\"}";
    }
}


package com.tlwl.router;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

@Path("/echo")
public class Echo extends BaseRs{
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{tablename}")
    public String rsGet(@PathParam("tablename") String tablename, @Context UriInfo ui){
        return "echo say: " + tablename;
    }
}


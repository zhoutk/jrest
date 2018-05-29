package com.tlwl.router;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.tlwl.utils.RouterHelper;
import java.util.Map;

@Path("/rs")
public class Rs {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{tablename}/{id}")
    public String rsGetId(@PathParam("tablename") String tablename, @PathParam("id") String id, @Context UriInfo ui){
        Map queryParams = ui.getQueryParameters();
        return RouterHelper.process(tablename, queryParams, id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{tablename}")
    public String rsGetId(@PathParam("tablename") String tablename, @Context UriInfo ui){
        Map queryParams = ui.getQueryParameters();
        return RouterHelper.process(tablename, queryParams);
    }
}


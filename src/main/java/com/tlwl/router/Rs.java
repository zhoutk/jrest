package com.tlwl.router;

import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;


import com.tlwl.main.GlobalConst;
import com.tlwl.utils.RouterHelper;
import org.json.JSONObject;

import java.util.Map;

@Path("/rs")
public class Rs extends BaseRs {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{tablename}/{id}")
    public String rsGet(@PathParam("tablename") String tablename, @PathParam("id") String id, @Context UriInfo ui){
        Map queryParams = ui.getQueryParameters();
        return RouterHelper.process(tablename, queryParams, GlobalConst.RESTMETHOD.GET, id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{tablename}")
    public String rsGet(@PathParam("tablename") String tablename, @Context UriInfo ui){
        Map queryParams = ui.getQueryParameters();
        return RouterHelper.process(tablename, queryParams, GlobalConst.RESTMETHOD.GET);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{tablename}/{id}")
    public String rsPost(@PathParam("tablename") String tablename, @PathParam("id") String id, Object request){
        return RouterHelper.process(tablename, (Map)request, GlobalConst.RESTMETHOD.POST, id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{tablename}")
    public String rsPost(@PathParam("tablename") String tablename, @Context ContainerRequestContext cxt, Object request){
        JSONObject session = (JSONObject) cxt.getProperty("session");
        return RouterHelper.process(tablename, (Map)request, GlobalConst.RESTMETHOD.POST, "", session);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{tablename}/{id}")
    public String rsPut(@PathParam("tablename") String tablename, @PathParam("id") String id, Object request){
        return RouterHelper.process(tablename, (Map)request, GlobalConst.RESTMETHOD.PUT, id);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{tablename}/{id}")
    public String rsDel(@PathParam("tablename") String tablename, @PathParam("id") String id, @Context UriInfo ui){
        Map queryParams = ui.getQueryParameters();
        return RouterHelper.process(tablename, queryParams, GlobalConst.RESTMETHOD.DELETE, id);
    }
}


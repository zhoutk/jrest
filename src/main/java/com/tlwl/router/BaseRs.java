package com.tlwl.router;

import com.tlwl.main.GlobalConst;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

public class BaseRs {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{tablename}/{id}")
    public String rsGet(@PathParam("tablename") String tablename, @PathParam("id") String id, @Context UriInfo ui){
        return GlobalConst.ERRORS.getJSONObject("204").toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{tablename}")
    public String rsGet(@PathParam("tablename") String tablename, @Context UriInfo ui){
        return GlobalConst.ERRORS.getJSONObject("204").toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String zget1(){
        return GlobalConst.ERRORS.getJSONObject("404").toString();
    }

    @GET
    @Path("{tablename:(.*)}/{id:(.*)}/{ps:(.*)}")
    @Produces(MediaType.APPLICATION_JSON)
    public String zget2(){
        return GlobalConst.ERRORS.getJSONObject("404").toString();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{tablename}/{id}")
    public String rsPost(@PathParam("tablename") String tablename, @PathParam("id") String id, @Context UriInfo ui){
        return GlobalConst.ERRORS.getJSONObject("204").toString();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{tablename}")
    public String rsPost(@PathParam("tablename") String tablename, @Context UriInfo ui){
        return GlobalConst.ERRORS.getJSONObject("204").toString();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String zpost1(){
        return GlobalConst.ERRORS.getJSONObject("404").toString();
    }

    @POST
    @Path("{tablename:(.*)}/{id:(.*)}/{ps:(.*)}")
    @Produces(MediaType.APPLICATION_JSON)
    public String zpost2(){
        return GlobalConst.ERRORS.getJSONObject("404").toString();
    }


    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{tablename}/{id}")
    public String rsPut(@PathParam("tablename") String tablename, @PathParam("id") String id, @Context UriInfo ui){
        return GlobalConst.ERRORS.getJSONObject("204").toString();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{tablename}")
    public String rsPut(@PathParam("tablename") String tablename, @Context UriInfo ui){
        return GlobalConst.ERRORS.getJSONObject("404").toString();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public String zput1(){
        return GlobalConst.ERRORS.getJSONObject("404").toString();
    }

    @PUT
    @Path("{tablename:(.*)}/{id:(.*)}/{ps:(.*)}")
    @Produces(MediaType.APPLICATION_JSON)
    public String zput2(){
        return GlobalConst.ERRORS.getJSONObject("404").toString();
    }


    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{tablename}/{id}")
    public String rsDel(@PathParam("tablename") String tablename, @PathParam("id") String id, @Context UriInfo ui){
        return GlobalConst.ERRORS.getJSONObject("204").toString();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{tablename}")
    public String rsDel(@PathParam("tablename") String tablename, @Context UriInfo ui){
        return GlobalConst.ERRORS.getJSONObject("404").toString();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String zdel1(){
        return GlobalConst.ERRORS.getJSONObject("404").toString();
    }

    @DELETE
    @Path("{tablename:(.*)}/{id:(.*)}/{ps:(.*)}")
    @Produces(MediaType.APPLICATION_JSON)
    public String zdel2(){
        return GlobalConst.ERRORS.getJSONObject("404").toString();
    }
}


package com.tlwl.router;

import com.tlwl.main.GlobalConst;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("{ps:(.*)}")
public class Zrs {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String zrsGet(){
        return GlobalConst.ERRORS.getJSONObject("404").toString();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String zrsPost(){
        return GlobalConst.ERRORS.getJSONObject("404").toString();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public String zrsPut(){
        return GlobalConst.ERRORS.getJSONObject("404").toString();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String zrsDelete(){
        return GlobalConst.ERRORS.getJSONObject("404").toString();
    }
}


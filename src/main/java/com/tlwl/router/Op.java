package com.tlwl.router;

import com.tlwl.main.GlobalConst;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

@Path("/op")
public class Op extends BaseRs{

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{tablename}")
    public String rsPost(@PathParam("tablename") String tablename, @Context UriInfo ui){
        return GlobalConst.OPSUCCESS.toString();
    }
}


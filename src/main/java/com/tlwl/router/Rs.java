package com.tlwl.router;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.tlwl.db.BaseDao;
import com.tlwl.db.mysql.MysqlDao;
import org.json.JSONObject;

@Path("/rs")
public class Rs {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{name}")
    public String hello(@PathParam("name") String name){
        BaseDao idao = new MysqlDao();
        JSONObject rs = idao.retrieve(name, null, null, null);
        return rs.toString();
    }
}


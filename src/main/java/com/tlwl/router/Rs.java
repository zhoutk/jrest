package com.tlwl.router;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.tlwl.db.BaseDao;
import com.tlwl.db.mysql.MysqlDao;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import java.util.List;

@Path("/rs")
public class Rs {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{name}")
    public Object hello(@PathParam("name") String name){
        BaseDao idao = new MysqlDao();
        List<Object> rs = idao.retrieve("1", null, null, null);
        return rs;
    }
}


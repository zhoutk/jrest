package com.tlwl.router;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.tlwl.db.BaseDao;
import com.tlwl.db.IDao;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

@Path("/rs")
public class Rs {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{tablename}/{id}")
    public String rsGetId(@PathParam("tablename") String tablename, @Context UriInfo ui){
        Map queryParams = ui.getQueryParameters();
        JSONObject params = new JSONObject();

        for(Object al : queryParams.keySet()){
            String key = al.toString();
            LinkedList value = (LinkedList)queryParams.get(key);
            String[] arr = new String[value.size()];
            Object bl = value.removeFirst();
            int i = 0;
            do{
                arr[i++] = (String) bl;
                if(value.size() > 0)
                    bl = value.removeFirst();
                else
                    bl = null;
            }while (bl != null);
            params.put(key, StringUtils.join(arr, ','));
        }
        IDao dao = new BaseDao(tablename);
        JSONObject rs = dao.retrieve(null, params, null, null);

        return rs.toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{name}")
    public String rsGetId(@PathParam("name") String name){
        //IDao idao = new BaseDao();
        //JSONObject rs = idao.retrieve(name, null, null, null);

        JSONObject rs = new JSONObject();
        rs.put("name", name);

        return rs.toString();
    }
}


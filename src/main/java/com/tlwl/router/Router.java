package com.tlwl.router;

import com.tlwl.db.BaseDao;
import com.tlwl.db.IDao;
import com.tlwl.main.GlobalConst;
import com.tlwl.utils.Tools;
import org.json.JSONObject;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

@Provider
public class Router implements ContainerResponseFilter {
    private static final String [] cmds = new String[] {"echo", "rs", "op"};
    @Override
    public void filter(ContainerRequestContext res, ContainerResponseContext req) throws IOException {
        String token = res.getHeaderString("Authorization");
        String errMessage = "";
        JSONObject rs = null;
        JSONObject payload = null, params = null;
        String id = null;
        res.setProperty("session", "{}");                               //important
        if(token != null && token.length() > 4){                        //decode jwt payload
            token = token.substring(4);
            payload = Tools.decodeJsonWebToken(token);
        }

        String method = res.getMethod();
        String queryUrl = res.getUriInfo().getPath();
        String [] urls = queryUrl.split("/");
        int urLen =  urls.length;
        if(urLen > 1 && urLen < 4 && Arrays.asList(cmds).contains(urls[0])){
            String tablename = urls[1];
            if(payload == null && method != "GET" && urls[0].equals("rs")){
                setResponse(req, GlobalConst.ERRORS.getJSONObject("601").toString());
                return;
            }
            if(method.equals("POST") || method.equals("PUT") && urLen == 3)
            {
                params = RouterHelper.getBodyParams(res);
            }else if(method.equals("GET") || method.equals("DELETE") && urLen == 3){
                params = RouterHelper.getQueryParams(res.getUriInfo().getQueryParameters());
            }else {
                setResponse(req, GlobalConst.ERRORS.getJSONObject("404").toString());
                return;
            }
            if((method.equals("POST") || method.equals("PUT")) &&(params == null || params.length() == 0)){
                setResponse(req, GlobalConst.ERRORS.getJSONObject("301").toString());
                return;
            }
            if(urLen > 2){
                id = urls[2];
                if(method.equals("GET") || method.equals("POST")){
                    params.put("id", id);
                }
            }

            if(urls[0].equals("rs")) {
                IDao dao;
                String clsName = tablename.substring(0, 1).toUpperCase() + tablename.substring(1);
                try {
                    Class cls = Class.forName("com.tlwl.dao." + clsName);
                    Constructor constructor = cls.getConstructor(String.class);
                    dao = (IDao) constructor.newInstance(tablename);
                } catch (Exception ex) {
                    dao = new BaseDao(tablename);
                    if (!ex.toString().startsWith("java.lang.ClassNotFoundException")) {
                        errMessage = ex.getMessage();
                        ex.printStackTrace();
                    }
                }
                switch (method) {
                    case "GET":
                        rs = dao.retrieve(null, params, null, payload);
                        break;
                    case "POST":
                        rs = dao.create(null, params, null, payload);
                        break;
                    case "PUT":
                        rs = dao.update(id, params, null, payload);
                        break;
                    case "DELETE":
                        rs = dao.delete(id, params, null, payload);
                        break;
                }
            }else{
                try {
                    Class cls = Class.forName("com.tlwl.auth." + urls[0].substring(0, 1).toUpperCase() + urls[0].substring(1));
                    Constructor constructor = cls.getConstructor();
                    Object obj = constructor.newInstance();
                    Method sf = cls.getMethod(urls[1], params.getClass() , payload.getClass());
                    rs = (JSONObject) sf.invoke(obj, params, payload);
                }catch (Exception ex){
                    errMessage = ex.getMessage();
                    ex.printStackTrace();
                }
            }
        }else{          //404
            setResponse(req, GlobalConst.ERRORS.getJSONObject("404").toString());
            return;
        }
        if(errMessage.length() > 0){            //异常
            setResponse(req, GlobalConst.ERRORS.getJSONObject("500").put("message", errMessage).toString());
        }else
            setResponse(req, rs.toString());
        System.out.println(payload == null ? "{}" : payload.toString());
    }

    private void setResponse(ContainerResponseContext req, String payload){
        req.setEntity(payload,null,MediaType.APPLICATION_JSON_TYPE);
    }
}
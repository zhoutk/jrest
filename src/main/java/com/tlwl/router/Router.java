package com.tlwl.router;

import com.tlwl.main.GlobalConst;
import com.tlwl.utils.Tools;
import org.json.JSONObject;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Arrays;

@Provider
public class Router implements ContainerResponseFilter {
    private static final String [] cmds = new String[] {"echo", "rs", "op"};
    @Override
    public void filter(ContainerRequestContext res, ContainerResponseContext req) throws IOException {
        String token = res.getHeaderString("Authorization");
        JSONObject payload = null, params = null;
        String id = null;
        if(token != null && token.length() > 4){                        //decode jwt payload
            token = token.substring(4);
            payload = Tools.decodeJsonWebToken(token);
            res.setProperty("session", payload == null ? "{}" : payload);
        }

        String method = res.getMethod();
        String queryUrl = res.getUriInfo().getPath();
        String [] urls = queryUrl.split("/");
        int urLen =  urls.length;
        if(urLen > 1 && urLen < 4 && Arrays.asList(cmds).contains(urls[0])){
            if(payload == null && method != "GET" && urls[0].equals("rs")){
                setResponse(req, GlobalConst.ERRORS.getJSONObject("601").toString());
                return;
            }
            if(method.equals("POST") || method.equals("PUT"))
            {
                params = RouterHelper.getBodyParams(res);
            }else if(method.equals("GET") || method.equals("DELETE")){
                params = RouterHelper.getQueryParams(res.getUriInfo().getQueryParameters());
            }else {
                setResponse(req, GlobalConst.ERRORS.getJSONObject("404").toString());
                return;
            }
            if(urLen > 2){
                id = urls[2];
                if(method.equals("GET") || method.equals("POST")){
                    params.put("id", id);
                }
            }
        }else{          //404
            setResponse(req, GlobalConst.ERRORS.getJSONObject("404").toString());
        }

        setResponse(req, payload.toString());
        System.out.println(payload == null ? "{}" : payload.toString());
    }

    private void setResponse(ContainerResponseContext req, String payload){
        req.setEntity(payload,null,MediaType.APPLICATION_JSON_TYPE);
    }
}
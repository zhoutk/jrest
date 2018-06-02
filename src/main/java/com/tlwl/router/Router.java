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

@Provider
public class Router implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext res, ContainerResponseContext req) throws IOException {
        String token = res.getHeaderString("Authorization");
        JSONObject payload = null;
        if(token != null && token.length() > 4){                        //session valid
            token = token.substring(4);
            payload = Tools.decodeJsonWebToken(token);
            res.setProperty("session", payload == null ? "{}" : payload);
        }else{                                                          //session not valid

        }

        if(payload == null){
            setResponse(req, GlobalConst.ERRORS.getJSONObject("601").toString());
        }else
            setResponse(req, payload.toString());
        System.out.println(payload == null ? "{}" : payload.toString());
    }

    private void setResponse(ContainerResponseContext req, String payload){
        req.setEntity(payload,null,MediaType.APPLICATION_JSON_TYPE);
    }
}
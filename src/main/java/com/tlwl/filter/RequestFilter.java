package com.tlwl.filter;

import com.tlwl.utils.Tools;
import org.json.JSONObject;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class RequestFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        String token = requestContext.getHeaderString("Authorization");
        if(token != null && token.length() > 4){                        //session valid
            token = token.substring(4);
            JSONObject payload = Tools.decodeJsonWebToken(token);
            requestContext.setProperty("session", payload);
        }else{                                                          //session not valid

        }

        responseContext.setEntity(requestContext.getProperty("session").toString(),null,MediaType.APPLICATION_JSON_TYPE);
        System.out.println(requestContext.getProperty("session").toString());
    }
}
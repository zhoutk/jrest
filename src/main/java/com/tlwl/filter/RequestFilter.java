package com.tlwl.filter;

import com.tlwl.utils.Tools;
import org.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

public class RequestFilter implements ContainerRequestFilter {
    @Context UriInfo ui;
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String token = requestContext.getHeaderString("Authorization");
        if(token != null && token.length() > 4){                        //session valid
            token = token.substring(4);
            JSONObject payload = Tools.decodeJsonWebToken(token);
            requestContext.setProperty("session", payload);
        }else{                                                          //session not valid

        }
    }
}
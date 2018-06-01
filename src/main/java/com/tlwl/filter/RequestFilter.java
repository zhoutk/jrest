package com.tlwl.filter;

import com.tlwl.utils.Tools;
import org.json.JSONObject;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;

public class RequestFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String token = requestContext.getHeaderString("Authorization");
        if(token != null && token.length() > 4){
            token = token.substring(4);
            JSONObject payload = Tools.decodeJsonWebToken(token);
            requestContext.setProperty("session", payload);
        }
    }
}
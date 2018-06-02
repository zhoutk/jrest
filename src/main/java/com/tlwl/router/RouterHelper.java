package com.tlwl.router;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.message.internal.ReaderWriter;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.container.ContainerRequestContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

class RouterHelper {
    private final static String[] ParamsLimits = {"search", "page", "size", "order", "lks", "ins", "group", "count", "sum", "ors", "fields"};

    static JSONObject getBodyParams(ContainerRequestContext containerRequestContext) {
        String body;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = containerRequestContext.getEntityStream();
        try {
            ReaderWriter.writeTo(in, out);
//            containerRequestContext
//                    .setEntityStream(new ByteArrayInputStream(out.toByteArray()));
            body = new String(out.toByteArray());
        } catch (IOException ex) {
            body = "";
        }
        return body.length() == 0 ? null : new JSONObject(body);
    }

    static JSONObject getQueryParams(Map queryParams){
        JSONObject params = new JSONObject();
        try {
            for (Object al : queryParams.keySet()) {
                String key = al.toString().toLowerCase();
                LinkedList value = (LinkedList) queryParams.get(key);

                String[] arr = new String[value.size()];
                Object bl = value.removeFirst();
                int i = 0;
                do {
                    arr[i++] = bl.toString();
                    if (value.size() > 0)
                        bl = value.removeFirst();
                    else
                        bl = null;
                } while (bl != null);
                Boolean flagOflimit = Arrays.asList(ParamsLimits).contains(key);
                if (arr.length > 1 && flagOflimit) {    //限制字段报错
                    return null;
                } else if (arr.length == 1 && flagOflimit) {            //处理特殊参数
                    try {
                        if (key.equals("search") || key.equals("page") || key.equals("size")) {
                            params.put(key, Integer.parseInt(arr[0]));
                        } else if (key.equals("count") || key.equals("sum") || key.equals("fields")) {
                            params.put(key, new JSONArray(arr[0]));
                        } else if (key.equals("lks") || key.equals("ins") || key.equals("ors")) {
                            params.put(key, new JSONArray(arr[0]));
                        } else {
                            params.put(key, arr[0]);
                        }
                    } catch (Exception ex) {
                        return null;
                    }
                } else
                    params.put(key, StringUtils.join(arr, ','));
            }
        } catch (Exception ex) {
            params = null;
            ex.printStackTrace();
        }
        return params;
    }
}

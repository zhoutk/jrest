package com.tlwl.router;

import com.tlwl.db.BaseDao;
import com.tlwl.db.IDao;
import com.tlwl.main.GlobalConst;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

import static com.tlwl.main.GlobalConst.RESTMETHOD.*;

public class RouterHelper {
    private final static String[] ParamsLimits = {"search", "page", "size", "order", "lks", "ins", "group", "count", "sum", "ors", "fields"};

    public static String process(String tablename, Map queryParams, GlobalConst.RESTMETHOD method, String id) {
        return process(tablename, queryParams, method, id, null);
    }

    public static String process(String tablename, Map queryParams, GlobalConst.RESTMETHOD method, String id, JSONObject session) {
        JSONObject rs = null;
        String errMessage = "";
        try {
            JSONObject params = new JSONObject();

            for (Object al : queryParams.keySet()) {
                String key = al.toString().toLowerCase();
                LinkedList value = new LinkedList();
                if (method == GET || method == DELETE)
                    value = (LinkedList) queryParams.get(key);
                else
                    value.push(key.endsWith("_json") ? (queryParams.get(key).toString().startsWith("[") ?
                            new JSONArray((java.util.Collection<Object>)queryParams.get(key)) :
                            new JSONObject((Map) queryParams.get(key))) : queryParams.get(key));
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
                    return GlobalConst.ERRORS.getJSONObject("301").toString();
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
                        return GlobalConst.ERRORS.getJSONObject("301").put("message", ex.getMessage()).toString();
                    }
                } else
                    params.put(key, StringUtils.join(arr, ','));
            }
            if (id.length() > 0 && (method == POST || method == GET))
                params.put("id", id);

            IDao dao;
            String clsName = tablename.substring(0, 1).toUpperCase() + tablename.substring(1);
            try {
                Class d1 = Class.forName("com.tlwl.dao." + clsName);
                Constructor constructor = d1.getConstructor(String.class);
                dao = (IDao) constructor.newInstance(tablename);
            }catch (Exception ex){
                dao = new BaseDao(tablename);
                if(!ex.toString().startsWith("java.lang.ClassNotFoundException")){
                    errMessage = ex.getMessage();
                    ex.printStackTrace();
                }
            }
            switch (method) {
                case GET:
                    rs = dao.retrieve(null, params, null, session);
                    break;
                case POST:
                    rs = dao.create(null, params, null, session);
                    break;
                case PUT:
                    rs = dao.update(id, params, null, session);
                    break;
                case DELETE:
                    rs = dao.delete(id, params, null, session);
                    break;
            }
        } catch (Exception ex) {
            errMessage = ex.getMessage();
            ex.printStackTrace();
        }
        if(errMessage.length() > 0) {
            rs.put("code", 500);
            rs.put("message", errMessage);
        }
        return rs.toString();
    }

    public static String process(String tablename, Map queryParams, GlobalConst.RESTMETHOD method) {
        return process(tablename, queryParams, method, "");
    }
}

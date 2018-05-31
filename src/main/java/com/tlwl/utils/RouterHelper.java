package com.tlwl.utils;

import com.tlwl.db.BaseDao;
import com.tlwl.db.IDao;
import com.tlwl.main.GlobalConst;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

public class RouterHelper {
    private final static String [] ParamsLimits = {"search", "page", "size", "order", "lks", "ins", "group", "count", "sum", "ors", "fields"};
    public static String process(String tablename, Map queryParams, GlobalConst.RESTMETHOD method, String id){
        JSONObject params = new JSONObject();

        for(Object al : queryParams.keySet()){
            String key = al.toString().toLowerCase();
            LinkedList value = new LinkedList();
            if(method == GlobalConst.RESTMETHOD.GET)
                value = (LinkedList)queryParams.get(key);
            else
                value.push(key.endsWith("_json") ? (new JSONObject((Map)queryParams.get(key))) : queryParams.get(key));
            String[] arr = new String[value.size()];
            Object bl = value.removeFirst();
            int i = 0;
            do{
                arr[i++] = bl.toString();
                if(value.size() > 0)
                    bl = value.removeFirst();
                else
                    bl = null;
            }while (bl != null);
            Boolean flagOflimit = Arrays.asList(ParamsLimits).contains(key);
            if(arr.length > 1 && flagOflimit){    //限制字段报错
                return GlobalConst.ERRORS.getJSONObject("301").toString();
            }else if(arr.length == 1 && flagOflimit){            //处理特殊参数
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
                }catch (Exception ex){
                    return GlobalConst.ERRORS.getJSONObject("301").put("message", ex.getMessage()).toString();
                }
            }else
                params.put(key, StringUtils.join(arr, ','));
        }
        if(id.length() > 0)
            params.put("id", id);
        IDao dao = new BaseDao(tablename);
        JSONObject rs = null;
        switch (method){
            case GET:
                rs = dao.retrieve(null, params, null, null);
                break;
            case POST:
                rs = dao.create(null, params, null, null);
                break;
            case PUT:
                rs = dao.retrieve(null, params, null, null);
                break;
            case DELETE:
                rs = dao.retrieve(null, params, null, null);
                break;
        }
        return rs.toString();
    }

    public static String process(String tablename, Map queryParams, GlobalConst.RESTMETHOD method){
        return process(tablename, queryParams, method, "");
    }
}

package com.tlwl.utils;

import com.tlwl.db.BaseDao;
import com.tlwl.db.IDao;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Map;

public class RouterHelper {
    public static String process(String tablename, Map queryParams, String id){
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
        if(id.length() > 0)
            params.put("id", id);
        IDao dao = new BaseDao(tablename);
        JSONObject rs = dao.retrieve(null, params, null, null);
        return rs.toString();
    }

    public static String process(String tablename, Map queryParams){
        return process(tablename, queryParams, "");
    }
}

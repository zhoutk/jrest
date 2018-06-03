package com.tlwl.dao;

import com.tlwl.db.BaseDao;
import org.json.JSONArray;
import org.json.JSONObject;

public class MemberInfo extends BaseDao {
    private String table;
    public MemberInfo(){
        super();
        this.table = "";
    }
    public MemberInfo(String tablename){
        super(tablename);
        this.table = tablename;
    }
    public JSONObject retrieve(String id, JSONObject params, JSONArray fields, JSONObject session){
        JSONObject rs = new BaseDao("member").retrieve(id, params, fields, session);
        return rs;
    }
}

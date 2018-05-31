package com.tlwl.dao;

import com.tlwl.db.BaseDao;
import org.json.JSONArray;
import org.json.JSONObject;

public class Member extends BaseDao {
    private String table;
    public Member(){
        super("");
    }
    public Member(String tablename){
        super(tablename);
        this.table = tablename;
    }
    public JSONObject retrieve(String id, JSONObject params, JSONArray fields, Object session){
        JSONObject rs = new BaseDao(this.table).retrieve(id, params, fields, session);
        return rs;
    }
}

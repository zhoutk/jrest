package com.tlwl.dao;

import com.tlwl.db.BaseDao;
import org.json.JSONArray;
import org.json.JSONObject;
import static com.tlwl.main.GlobalConst.uuid;

public class Uuid extends BaseDao {
    private String table;
    public Uuid(){
        super("");
        this.table = "";
    }
    public Uuid(String tablename){
        super(tablename);
        this.table = tablename;
    }

    public JSONObject retrieve(String id, JSONObject params, JSONArray fields, Object session){
        return new JSONObject("{\"id\":\""+uuid()+"\"}");
    }
}

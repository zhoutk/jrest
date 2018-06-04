package com.tlwl.dao;

import com.tlwl.db.BaseDao;
import com.tlwl.utils.Tools;
import org.json.JSONArray;
import org.json.JSONObject;
import static com.tlwl.main.GlobalConst.uuid;

public class Uuid extends BaseDao {
    private String table;
    public Uuid(){
        super();
        this.table = "";
    }
    public Uuid(String tablename){
        super(tablename);
        this.table = tablename;
    }

    public JSONObject retrieve(String id, JSONObject params, JSONArray fields, JSONObject session){
//        String dd = Tools.encodeJsonWebToken(new JSONObject("{\"copyright\":\"tlwl\",\"iat\":"+Math.floor(System.currentTimeMillis()/1000)+"}"));
//        return new JSONObject("{\"payload\":\""+dd+"\"}");

        JSONArray vs = new JSONArray();
        vs.put("10000001");
        JSONObject ps = new JSONObject();
        ps.put("status", 1).put("is_rank", 1);
        return new BaseDao().querySql("select * from role where id = ? ", vs, ps);


    }
}

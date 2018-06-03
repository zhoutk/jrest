package com.tlwl.db;

import com.tlwl.db.mysql.MysqlDao;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.tlwl.main.GlobalConst.uuid;

public class BaseDao implements IDao {
    private String table;
    public BaseDao(){ this.table = ""; }
    public BaseDao(String tablename){
        this.table = tablename;
    }

    public JSONObject retrieve(String id, JSONObject params, JSONArray fields, JSONObject session){
        JSONObject rs = MysqlDao.select(this.table, params, fields);
        return rs;
    }
    public JSONObject create(String id, JSONObject params, JSONArray fields, JSONObject session){
        boolean is_auto_id = params.has("auto_id") && params.get("auto_id").toString().equals("1");
        if(!params.has("id")){                          //即ID不存在，因为参数处理时，若ID存在，就已经加入参数中
            if(!is_auto_id){
                id = uuid();
                params.put("id", id);
            }
            if(params.has("auto_id"))                   //保证auto_id值不正确时，不出程序错误
                params.remove("auto_id");
        }
        JSONObject rs = MysqlDao.insert(this.table, params);
        if(!is_auto_id)
            rs.put("id", id);
        return rs;
    }
    public JSONObject update(String id, JSONObject params, JSONArray fields, JSONObject session){
        JSONObject rs = MysqlDao.update(this.table, params, id);
        return rs;
    }
    public JSONObject delete(String id, JSONObject params, JSONArray fields, JSONObject session){
        JSONObject rs = MysqlDao.delete(this.table, id);
        return rs;
    }
}

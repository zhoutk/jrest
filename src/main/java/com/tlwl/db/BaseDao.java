package com.tlwl.db;

import com.tlwl.db.mysql.MysqlDao;
import org.json.JSONArray;
import org.json.JSONObject;

public class BaseDao implements IDao {
    private String table;

    public BaseDao(){
        this.table = "";
    }

    public BaseDao(String tablename){
        this.table = tablename;
    }
    public JSONObject retrieve(String id, JSONObject params, JSONArray fields, Object session){
        JSONObject rs = MysqlDao.select(this.table, params, null);
        return rs;
    }
    public JSONObject create(String id, JSONObject params, JSONArray fields, Object session){
        JSONObject rs = MysqlDao.insert(this.table, params);
        return rs;
    }
    public JSONObject update(String id, JSONObject params, JSONArray fields, Object session){
        JSONObject rs = MysqlDao.update(this.table, params, id);
        return rs;
    }
    public JSONObject delete(String id, JSONObject params, JSONArray fields, Object session){
        JSONObject rs = MysqlDao.delete(this.table, id);
        return rs;
    }
}

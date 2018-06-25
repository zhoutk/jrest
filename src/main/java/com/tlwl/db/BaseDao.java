package com.tlwl.db;

import com.tlwl.db.mysql.MysqlDao;
import org.json.JSONArray;
import org.json.JSONObject;
import com.tlwl.main.GlobalConst;

import static com.tlwl.main.GlobalConst.uuid;

public class BaseDao implements IDao {
    private String table;
    public BaseDao(){ this.table = ""; }
    public BaseDao(String tablename){
        this.table = tablename;
    }

    public JSONObject retrieve(String id, JSONObject params, JSONArray fields, JSONObject session){
        params = params == null? new JSONObject():params;
        fields = fields == null? new JSONArray():fields;
        JSONObject rs = MysqlDao.select(this.table, params, fields);
        return rs;
    }
    public JSONObject create(String id, JSONObject params, JSONArray fields, JSONObject session){
        if(params == null || params.length() == 1 && params.has("id"))
            return GlobalConst.getErrorsJSON(301);
        session = session == null? new JSONObject():session;
        boolean is_auto_id = params.has("auto_id") && params.get("auto_id").toString().equals("1");
        boolean is_save_u_id = params.has("save_u_id") && params.get("save_u_id").toString().equals("1");
        if(params.has("auto_id"))
            params.remove("auto_id");
        if(params.has("save_u_id"))
            params.remove("save_u_id");
        if(is_save_u_id && session.has("userid")){
            params.put("u_id", session.getString("userid"));
        }
        if(!params.has("id")){                          //即ID不存在，因为参数处理时，若ID存在，就已经加入参数中
            if(!is_auto_id){
                id = uuid();
                params.put("id", id);
            }
        }
        JSONObject rs = MysqlDao.insert(this.table, params);
        if(!is_auto_id)
            rs.put("id", id);
        return rs;
    }
    public JSONObject update(String id, JSONObject params, JSONArray fields, JSONObject session){
        if(params == null || params.length() == 0)
            return GlobalConst.getErrorsJSON(301);
        JSONObject rs = MysqlDao.update(this.table, params, id);
        return rs;
    }
    public JSONObject delete(String id, JSONObject params, JSONArray fields, JSONObject session){
        JSONObject rs = MysqlDao.delete(this.table, id);
        return rs;
    }

    public JSONObject querySql(String sql, JSONArray values, JSONObject params){
        params = params == null? new JSONObject():params;
        values = values == null? new JSONArray():values;
        JSONObject rs = MysqlDao.querySql(sql, values, params);
        return rs;
    }

    public JSONObject querySql(String sql, JSONArray values){
        values = values == null? new JSONArray():values;
        JSONObject rs = MysqlDao.querySql(sql, values, null);
        return rs;
    }

    public JSONObject execSql(String sql, JSONArray values){
        values = values == null? new JSONArray():values;
        JSONObject rs = MysqlDao.execSql(sql, values);
        return rs;
    }

    public JSONObject insertBatch(String tablename, JSONArray values){
        values = values == null? new JSONArray():values;
        JSONObject rs = MysqlDao.insertBatch(tablename, values);
        return rs;
    }

    public JSONObject transGo(JSONArray objs){
        if(objs == null || objs.length() == 0)
            return GlobalConst.getErrorsJSON(301);
        JSONObject rs = MysqlDao.transGo(objs);
        return rs;
    }

}

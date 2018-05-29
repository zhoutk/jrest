package com.tlwl.db;

import com.tlwl.db.mysql.MysqlDao;
import org.json.JSONObject;

public class BaseDao implements IDao {
    private String table;
    public BaseDao(String tablename){
        this.table = tablename;
    }
    public JSONObject retrieve(String id, JSONObject params, String [] fields, Object session){
        JSONObject rs = MysqlDao.select(this.table, params, null);
        return rs;
    }
}

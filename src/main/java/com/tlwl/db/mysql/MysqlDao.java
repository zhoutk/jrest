package com.tlwl.db.mysql;

import com.tlwl.db.BaseDao;
import org.json.JSONObject;

public class MysqlDao implements BaseDao {
    public JSONObject retrieve(String id, Object params, String [] fields, Object session){
        JSONObject rs = DbHelper.select(id);
        return rs;
    }
}

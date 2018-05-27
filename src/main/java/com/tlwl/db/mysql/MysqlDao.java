package com.tlwl.db.mysql;

import com.tlwl.db.BaseDao;
import java.util.List;

public class MysqlDao implements BaseDao {
    public List<Object> retrieve(String id, Object params, String [] fields, Object session){
        List<Object> rs = DbHelper.select();
        return rs;
    }
}

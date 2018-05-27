package com.tlwl.db;

import org.json.JSONObject;

public interface BaseDao {
    JSONObject retrieve(String id, Object params, String [] fields, Object session);
}

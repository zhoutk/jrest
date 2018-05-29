package com.tlwl.db;

import org.json.JSONObject;

public interface IDao {
    JSONObject retrieve(String id, JSONObject params, String [] fields, Object session);
}

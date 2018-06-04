package com.tlwl.db;

import org.json.JSONArray;
import org.json.JSONObject;

public interface IDao {
    JSONObject retrieve(String id, JSONObject params, JSONArray fields, JSONObject session);
    JSONObject create(String id, JSONObject params, JSONArray fields, JSONObject session);
    JSONObject update(String id, JSONObject params, JSONArray fields, JSONObject session);
    JSONObject delete(String id, JSONObject params, JSONArray fields, JSONObject session);
    JSONObject querySql(String sql, JSONArray values, JSONObject params);
    JSONObject querySql(String sql, JSONArray values);
    JSONObject execSql(String sql, JSONArray values);
    JSONObject insertBatch(String tablename, JSONArray values);
}

package com.tlwl.db;

import org.json.JSONArray;
import org.json.JSONObject;

public interface IDao {
    JSONObject retrieve(String id, JSONObject params, JSONArray fields, Object session);
    JSONObject create(String id, JSONObject params, JSONArray fields, Object session);
    JSONObject update(String id, JSONObject params, JSONArray fields, Object session);
    JSONObject delete(String id, JSONObject params, JSONArray fields, Object session);
}

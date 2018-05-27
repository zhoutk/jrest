package com.tlwl.db;

import java.util.List;

public interface BaseDao {
    List<Object> retrieve(String id, Object params, String [] fields, Object session);
}

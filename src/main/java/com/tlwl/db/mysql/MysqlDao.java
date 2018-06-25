package com.tlwl.db.mysql;

import com.mysql.cj.jdbc.ClientPreparedStatement;
import com.tlwl.main.GlobalConst;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.commons.lang3.RandomUtils;

import java.sql.*;
import java.util.Iterator;

public class MysqlDao {
    private static JSONObject dbConfs = GlobalConst.CONFIGS.getJSONObject("db_mysql_config");
    private static int PoolMaxSize = dbConfs.getInt("db_conn");
    private static Connection conn[] = new Connection[PoolMaxSize];

    private static Connection createConnection() {
        int index = RandomUtils.nextInt(0, dbConfs.getInt("db_conn"));
        if (conn[index] == null) {
            try {
                conn[index] = DriverManager.getConnection(
                        "jdbc:mysql://" + dbConfs.getString("db_host") + ":" + dbConfs.getInt("db_port") + "/" + dbConfs.getString("db_name") + "?" +
                                "user=" + dbConfs.getString("db_user") + "&password=" + dbConfs.getString("db_pass") +
                                "&characterEncoding=utf8&useSSL=false&autoReconnect=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai");
            } catch (SQLException ex) {
                // handle any errors
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            }
        }
        return conn[index];
    }

    public static JSONObject delete(String tablename, String id) {
        JSONArray values = new JSONArray();
        String sql = generateDeleteString(tablename);
        values.put(id);
        return execQuery(sql, values).put("id", id);
    }

    private static String generateDeleteString(String tablename) {
        return "DELETE from " + tablename + " WHERE id = ? ";
    }

    public static JSONObject update(String tablename, JSONObject params, String id) {
        JSONArray values = new JSONArray();
        String sql = generateUpdateString(tablename, params, values);
        values.put(id);
        return execQuery(sql, values).put("id", id);
    }

    private static String generateUpdateString(String tablename, JSONObject params, JSONArray values) {
        Iterator<String> ks = params.keys();
        String sql = "UPDATE " + tablename + " SET ";
        while (ks.hasNext()) {
            String key = ks.next();
            values.put(key.endsWith("_json") ? params.get(key).toString() : params.get(key));
            sql += key + " = ?" + (ks.hasNext() ? ", " : " WHERE id = ? ");
        }
        return sql;
    }

    public static JSONObject insert(String tablename, JSONObject params) {
        JSONArray values = new JSONArray();
        String sql = generateInsertString(tablename, params, values);
        return execQuery(sql, values);
    }

    private static String generateInsertString(String tablename, JSONObject params, JSONArray values) {
        Iterator<String> ks = params.keys();
        String[] fls = new String[params.length()];
        String[] vls = new String[params.length()];
        int index = 0;
        while (ks.hasNext()) {
            String key = ks.next();
            fls[index] = key;
            vls[index] = "?";
            values.put(key.endsWith("_json") ? params.get(key).toString() : params.get(key));
            index++;
        }
        return "INSERT INTO " + tablename + " ( " + StringUtils.join(fls, ',') + " ) VALUES ( " + StringUtils.join(vls, ',') + " ) ";
    }

    public static JSONObject select(String tablename, JSONObject params, JSONArray fields) {  //String tablename, Object params, String [] fields
        return query(tablename, params, fields, null, null);
    }

    public static JSONObject querySql(String sql, JSONArray values, JSONObject params) {
        return query("QuerySqlSelect", params, null, sql, values);
    }

    public static JSONObject execSql(String sql, JSONArray values) {
        return execQuery(sql, values);
    }

    public static JSONObject transGo(JSONArray objs) {
        String errMessage = "", sqlStr = "";
        int upCount = 0, opCount = 0;
        boolean isBreak = false;
        JSONArray values = null;
        Connection conn = createConnection();
        try {
            conn.setAutoCommit(false);
            System.out.println("Trans Go Start ... ");
            for (Object obj : objs) {
                JSONObject al = (JSONObject) obj;
                String method = al.getString("method");
                JSONObject params = null;
                values = new JSONArray();
                sqlStr = "";
                String id = al.get("id") == null ? "" : al.get("id").toString();
                if(al.has("sql")){
                    sqlStr = al.getString("sql");
                    if(al.has("params"))
                        values = al.getJSONArray("params");
                    if(!id.isEmpty())
                        values.put(id);
                }else{
                    if(!al.has("params") && !method.equals("Delete")) {
                        isBreak = true;
                        errMessage = "Params must not be empty.";
                        return GlobalConst.getErrorsJSON(301).put("message", "Params must not be empty.");
                    }
                    if(al.has("params")){
                        params = al.getJSONObject("params");
                    }
                    if(method.equals("Update") || method.equals("Delete")){
                        if (id.isEmpty() && params != null) {
                            id = params.get("id") == null ? "" : params.get("id").toString();
                        }
                        if(id.isEmpty()) {
                            isBreak = true;
                            errMessage = "Id must not be empty.";
                            return GlobalConst.getErrorsJSON(301).put("message", "Id must not be empty.");
                        }
                        if(params != null && params.has("id")) {
                            params.remove("id");
                        }
                    }else if(method.equals("Insert") && !id.isEmpty()){
                        params.put("id", id);
                    }
                    String tablename = al.getString("table");
                    if(tablename.isEmpty()) {
                        isBreak = true;
                        errMessage = "Tablename must not be empty.";
                        return GlobalConst.getErrorsJSON(301).put("message", "Tablename must not be empty.");
                    }
                    switch (method) {
                        case "Insert":
                            sqlStr = generateInsertString(tablename, al.getJSONObject("params"), values);
                            break;
                        case "Update":
                            sqlStr = generateUpdateString(tablename, al.getJSONObject("params"), values);
                            values.put(id);
                            break;
                        case "Delete":
                            sqlStr = generateDeleteString(tablename);
                            values.put(id);
                            break;
                        default:
                            isBreak = true;
                            errMessage = "Method is wrong.";
                            return GlobalConst.getErrorsJSON(301).put("message", "Method is wrong.");
                    }
                }
                upCount += processStatement(conn, values, sqlStr);
                System.out.println("Tras." + ++opCount + ": " + sqlStr + "; VALUES: " + values.toString());
            }
            conn.commit();
        } catch (Exception ex) {
            isBreak = true;
            errMessage += ex.getMessage();
            System.out.println("Trans Rollbak: " + errMessage);
            System.out.println("Fail Sql: " + sqlStr + "; VALUES: " + values.toString());
            try {
                conn.rollback();
                errMessage += "; rollbak success.";
            } catch (SQLException e) {
                errMessage += "; rollbak failure.";
                e.printStackTrace();
            }
        } finally {
            if(isBreak){
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("Trans Rollbak: " + errMessage);
                System.out.println("Trans Go Stop Failure.");
            }else
                System.out.println("Trans Go Stop Success.");
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                try {
                    conn.close();
                } catch (SQLException eclose) {
                    eclose.printStackTrace();
                }
            }
        }
        if (errMessage.isEmpty())
            return GlobalConst.getSuccessJSON().put("run operate count: ", opCount).put("update records count: ", upCount);
        else
            return GlobalConst.getErrorsJSON(500).put("message", errMessage);
    }

    private static int processStatement(Connection conn, JSONArray values, String sql) throws SQLException {
        Iterator ks = values.iterator();
        DBParams ps = new DBParams();
        int m = 0;
        while (ks.hasNext()) {
            ps.addParam(values.get(m));
            m++;
            ks.next();
        }
        PreparedStatement pst = conn.prepareStatement(sql);
        ps.prepareStatement(pst);
        pst.execute();
        return pst.getUpdateCount();
    }

    public static JSONObject insertBatch(String tablename, JSONArray values) {
        String sql = "INSERT INTO " + tablename, upStr = "";
        JSONArray vs = new JSONArray();
        for (int i = 0; i < values.length(); i++) {
            JSONObject ps = values.getJSONObject(i);
            Iterator<String> ks = ps.keys();
            int index = 0;
            String[] vls = new String[ps.length()];
            String[] fls = new String[ps.length()];
            while (ks.hasNext()) {
                String key = ks.next();
                if (i == 0) {
                    fls[index] = key;
                    upStr += key + " = values (" + key + "),";
                }
                vls[index] = "?";
                vs.put(key.endsWith("_json") ? ps.get(key).toString() : ps.get(key));
                index++;
            }
            if (i == 0)
                sql += " ( " + StringUtils.join(fls, ',') + " ) values ";
            sql += " ( " + StringUtils.join(vls, ',') + " ),";
        }
        sql = sql.substring(0, sql.length() - 1) + " ON DUPLICATE KEY UPDATE " + upStr.substring(0, upStr.length() - 1);
        return execQuery(sql, vs);
    }

    private static JSONObject query(String tablename, JSONObject params, JSONArray fields, String sql, JSONArray values) {
        if (values == null)
            values = new JSONArray();
        if (params == null)
            params = new JSONObject();
        String where = "";
        Boolean is_search = false;
        if (params.has("search")) {
            is_search = true;
            params.remove("search");
        }
        fields = params.has("fields") ? params.getJSONArray("fields") : fields;
        int page = params.has("page") ? Integer.parseInt(params.get("page").toString()) : 0;
        int size = params.has("size") ? Integer.parseInt(params.get("size").toString()) : GlobalConst.PAGESIZE;
        String order = params.has("order") ? params.getString("order") : "";
        JSONArray lks = params.has("lks") ? params.getJSONArray("lks") : null;
        JSONArray ins = params.has("ins") ? params.getJSONArray("ins") : null;
        String group = params.has("group") ? params.getString("group") : "";
        JSONArray count = params.has("count") ? params.getJSONArray("count") : null;
        JSONArray sum = params.has("sum") ? params.getJSONArray("sum") : null;
        JSONArray ors = params.has("ors") ? params.getJSONArray("ors") : null;

        if (params.has("fields"))
            params.remove("fields");
        if (params.has("group"))
            params.remove("group");
        if (params.has("order"))
            params.remove("order");
        if (params.has("page"))
            params.remove("page");
        if (params.has("size"))
            params.remove("size");

        if (count != null) {
            if (count.length() == 0 || count.length() % 2 == 1) {
                return GlobalConst.getErrorsJSON(301).put("message", "Format of count is error.");
            }
            params.remove("count");
        }
        if (sum != null) {
            if (sum.length() == 0 || sum.length() % 2 == 1) {
                return GlobalConst.getErrorsJSON(301).put("message", "Format of sum is error.");
            }
            params.remove("sum");
        }

        Iterator<String> ks = params.keys();
        while (ks.hasNext()) {
            String key = ks.next();
            if (!where.isEmpty())
                where += " and ";

            if (key.equals("ins")) {
                if (ins.length() < 2) {
                    return GlobalConst.getErrorsJSON(301).put("message", "Format of ins is error.");
                }
                String c = ins.remove(0).toString();
                where += c + " in ( ";
                do {
                    values.put(ins.remove(0));
                    where += "?" + (ins.length() > 0 ? "," : "");
                } while (ins.length() > 0);
                where += " ) ";
            } else if (key.equals("lks")) {
                if (lks.length() < 2) {
                    return GlobalConst.getErrorsJSON(301).put("message", "Format of lks is error.");
                }
                String val = lks.remove(0).toString();
                where += " ( ";
                do {
                    where += lks.remove(0).toString() + " like ? ";
                    where += lks.length() > 0 ? " or " : "";
                    values.put("%" + val + "%");
                } while (lks.length() > 0);
                where += " ) ";
            } else if (key.equals("ors")) {
                if (ors.length() < 2 || ors.length() % 2 == 1) {
                    return GlobalConst.getErrorsJSON(301).put("message", "Format of ors is error.");
                }
                where += " ( ";
                do {
                    where += ors.remove(0).toString() + " = ? ";
                    values.put(ors.remove(0));
                    where += ors.length() > 0 ? " or " : "";
                } while (ors.length() > 0);
                where += " ) ";
            } else {
                String value = params.get(key).toString();
                if (value.startsWith("<,") || value.startsWith("<=,") || value.startsWith(">,") ||
                        value.startsWith(">=,") || value.startsWith("<>,") || value.startsWith("=,")) {
                    String[] vls = value.split(",");
                    if (vls.length == 2) {
                        where += key + vls[0] + " ? ";
                        values.put(vls[1]);
                    } else if (vls.length == 4) {
                        where += key + vls[0] + " ? and " + key + vls[2] + " ? ";
                        values.put(vls[1]);
                        values.put(vls[3]);
                    } else {
                        if (where.endsWith(" and ")) {
                            where = where.substring(0, where.length() - 4);
                        }
                    }
                } else if (is_search) {
                    where += key + " like ? ";
                    values.put("%" + value + "%");
                } else {
                    where += key + " = ? ";
                    values.put(value);
                }
            }
        }

        String poly = "";
        if (count != null) {
            do {
                poly += ",count(" + count.remove(0) + ") as " + count.remove(0) + " ";
            } while (count.length() > 0);
        }
        if (sum != null) {
            do {
                poly += ",sum(" + sum.remove(0) + ") as " + sum.remove(0) + " ";
            } while (sum.length() > 0);
        }

        if (tablename == "QuerySqlSelect") {
            sql = sql + (where.isEmpty() ? "" : ((sql.toLowerCase().contains("where") ? " and " : " where ") + where));
        } else {
            String fls = "";
            if (fields != null && fields.length() > 0)
                fls = fields.join(",").replaceAll("\"", "");
            sql = "SELECT " + (fls.length() > 0 ? (fls + poly) : (poly.length() > 0 ? poly.substring(1) : "*")) + " FROM " + tablename;
            if (where != "") {
                sql += " WHERE " + where;
            }
        }

        if (group.length() > 0) {
            sql += " GROUP BY " + group;
        }
        if (order.length() > 0) {
            sql += " ORDER BY " + order;
        }

        if (page > 0) {
            page--;
            String sql1 = sql + " LIMIT " + page * size + "," + size;
            int index = sql.toUpperCase().lastIndexOf(" FROM ");
            int end = sql.toUpperCase().lastIndexOf(" ORDER BY ");
            String sql2 = "SELECT COUNT(*) count " + sql.substring(index, end > 0 ? end : sql.length());
            JSONObject rs1 = execQuery(sql1, values);
            JSONObject rs2 = execQuery(sql2, values);
            int ct = 0;
            if (rs2.getInt("code") == 200 && group.length() == 0) {
                ct = rs2.getJSONArray("rows").getJSONObject(0).getInt("count");
            } else if (group.length() > 0) {
                ct = rs2.getJSONArray("rows").length();
            }
            rs1.put("count", Math.ceil(ct / size));
            rs1.put("records", ct);
            return rs1;
        } else {
            JSONObject rs = execQuery(sql, values);
            rs.put("count", rs.getInt("code") == 200 ? 1 : 0);
            return rs;
        }
    }

    private static JSONObject execQuery(String sql, JSONArray values) {
        Boolean flag = true, execFlag = true;
        int errorCode = 0, updateCount = 0;
        long new_id = 0;
        String errorMessage = "";
        Connection conn = createConnection();
        JSONArray json = new JSONArray();
        if (conn != null) {
            PreparedStatement stmt = null;
            ResultSet rs = null;
            Iterator ks = values.iterator();
            DBParams ps = new DBParams();
            int m = 0;
            while (ks.hasNext()) {
                ps.addParam(values.get(m));
                m++;
                ks.next();
            }
            try {
                stmt = conn.prepareStatement(sql);
                ps.prepareStatement(stmt);
                execFlag = stmt.execute();
                if (execFlag) {
                    rs = stmt.getResultSet();
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int columnCount = rsmd.getColumnCount();
                    while (rs.next()) {
                        JSONObject jo = new JSONObject();
                        for (int i = 1; i <= columnCount; i++) {
                            String theKey = rsmd.getColumnLabel(i);
                            if (theKey.endsWith("_json")) {
                                String theValue = rs.getObject(i) == null ? null : rs.getObject(i).toString();
                                if (theValue != null && theValue.startsWith("["))
                                    jo.put(theKey, new JSONArray(theValue));
                                else if (theValue != null)
                                    jo.put(theKey, new JSONObject(theValue));
                            } else {
                                jo.put(theKey, rs.getObject(i));
                            }
                        }
                        json.put(jo);
                    }
                } else {
                    updateCount = stmt.getUpdateCount();
                    new_id = ((ClientPreparedStatement) stmt).getLastInsertID();
                }
                System.out.println("SQL: " + sql + "; VALUES: " + values.toString());
            } catch (SQLException ex) {
                flag = false;
                errorCode = ex.getErrorCode();
                errorMessage = ex.getMessage();
                System.out.println("SQL: " + sql + "; Code: " + ex.getErrorCode() + "; Message: " + ex.getMessage());
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException sqlEx) {
                        sqlEx.printStackTrace();
                    }
                }

                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException sqlEx) {
                        sqlEx.printStackTrace();
                    }
                }
            }
        } else {
            errorCode = 4221;
            errorMessage = "The database connect error.";
        }
        JSONObject result = new JSONObject();
        if (flag) {
            if (execFlag) {               //是查询语句
                result.put("code", json.length() > 0 ? 200 : 602);
                result.put("rows", json);
                result.put("records", json.length());
            } else {                      //是执行语句
                result.put("code", 200);
                result.put("update_count", updateCount);
                result.put("id", new_id);
            }
        } else {
            result.put("code", 500);
            result.put("errcode", errorCode);
            result.put("message", errorMessage);
        }
        return result;
    }
}

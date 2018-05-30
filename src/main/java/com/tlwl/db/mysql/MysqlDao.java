package com.tlwl.db.mysql;

import com.tlwl.main.GlobalConst;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.commons.lang3.RandomUtils;

import java.sql.*;
import java.util.Arrays;
import java.util.Iterator;

public class MysqlDao {
    private static JSONObject dbConfs = GlobalConst.CONFIGS.getJSONObject("db_mysql_config");
    private static int PoolMaxSize = dbConfs.getInt("db_conn_limit");
    private static Connection conn[] = new Connection[PoolMaxSize];

    private static Connection createConnection() {
        int index = RandomUtils.nextInt(0, dbConfs.getInt("db_conn_limit"));
        if (conn[index] == null) {
            try {
                conn[index] = DriverManager.getConnection(
                        "jdbc:mysql://" + dbConfs.getString("db_host") + ":" + dbConfs.getInt("db_port") + "/" + dbConfs.getString("db_name") + "?" +
                                "user=" + dbConfs.getString("db_user") + "&password=" + dbConfs.getString("db_passwd") + "&characterEncoding=utf8&useSSL=false&autoReconnect=true");
            } catch (SQLException ex) {
                // handle any errors
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            }
        }
        return conn[index];
    }

    public static JSONObject select(String tablename, JSONObject params, String[] fields) {  //String tablename, Object params, String [] fields
        return query(tablename, params, fields, null, null);
    }

    private static JSONObject query(String tablename, JSONObject params, String[] fields, String sql, JSONArray values) {
        if (values == null)
            values = new JSONArray();
        String where = "";
        Boolean is_search = false;
        if (params.has("search")) {
            is_search = true;
            params.remove("search");
        }
        int page = params.has("page") ? Integer.parseInt(params.getString("page")) : 0;
        int size = params.has("size") ? Integer.parseInt(params.getString("size")) : 0;
        String order = params.has("order") ? params.getString("order") : "";
        JSONArray like = params.has("lks") ? params.getJSONArray("lks") : null;
        JSONArray ins = params.has("ins") ? params.getJSONArray("ins") : null;
        String group = params.has("group") ? params.getString("group") : "";
        JSONArray count = params.has("count") ? params.getJSONArray("count") : null;
        JSONArray sum = params.has("sum") ? params.getJSONArray("sum") : null;
        JSONArray ors = params.has("ors") ? params.getJSONArray("ors") : null;

        if(count != null){
            if(count.length() == 0 || count.length() % 2 == 1){
                return GlobalConst.ERRORS.getJSONObject("301").put("message", "Format of count is error.");
            }
            params.remove("count");
        }
        if(sum != null){
            if(sum.length() == 0 || sum.length() % 2 == 1){
                return GlobalConst.ERRORS.getJSONObject("301").put("message", "Format of sum is error.");
            }
            params.remove("count");
        }

        Iterator<String> ks = params.keys();
        while (ks.hasNext()) {
            String key = ks.next();
            Object value = params.get(key);
            if (where != "")
                where += " and ";

            if(key.equals("ins")){
                if(ins.length() < 2){
                    return GlobalConst.ERRORS.getJSONObject("301").put("message", "Format of ins is error.");
                }
                String c = ins.remove(0).toString();
                String[] pSeat = new String[ins.length()];
                Arrays.fill(pSeat,"?");
                where += c + " in ( "+ StringUtils.join(pSeat, ',') +" ) ";
                do{
                    values.put(ins.remove(0));
                }while(ins.length() > 0);
            } else if (is_search) {
                where += key + " like ? ";
                values.put("%" + value + "%");
            } else {
                where += key + " = ? ";
                values.put(value);
            }
        }

        if (tablename == "QuerySqlSelect") {
            sql += "";
        } else {
            sql = "SELECT * FROM " + tablename;
            if (where != "") {
                sql += " WHERE " + where;
            }
        }

        return execQuery(sql, values);
    }

    private static JSONObject execQuery(String sql, JSONArray values) {
        Boolean flag = true;
        int errorCode = 0;
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
                if (stmt.execute()) {
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
                    System.out.println("SQL: " + sql + "; VALUES: " + values.toString());
                }
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
            result.put("code", 200);
            result.put("rows", json);
        } else {
            result.put("code", 500);
            result.put("errcode", errorCode);
            result.put("message", errorMessage);
        }
        return result;
    }
}

package com.tlwl.db.mysql;

import com.tlwl.main.GlobalConst;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.Iterator;

public class MysqlDao {
    private static Connection conn = null;
    private static Connection createConnection() {
        if(conn == null) {
            try {
                JSONObject dbConfs = GlobalConst.CONFIGS.getJSONObject("db_mysql_config");
                conn = DriverManager.getConnection(
                        "jdbc:mysql://" + dbConfs.getString("db_host") + ":" + dbConfs.getInt("db_port") + "/" + dbConfs.getString("db_name") + "?" +
                                "user=" + dbConfs.getString("db_user") + "&password=" + dbConfs.getString("db_passwd") + "&characterEncoding=utf8&useSSL=false&autoReconnect=true");
            } catch (SQLException ex) {
                // handle any errors
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            }
        }
        return conn;
    }

    public static JSONObject select(String tablename, JSONObject params, String [] fields){  //String tablename, Object params, String [] fields
        return query(tablename, params, fields, null, null);
    }

    private static JSONObject query(String tablename, JSONObject params, String[] fields, String sql, JSONArray values){
        if(values == null)
            values = new JSONArray();
        String where = "";
        Boolean is_search = false;
        if(params.has("search")){
            is_search = true;
            params.remove("search");
        }
        int page = params.has("page") ? Integer.parseInt(params.getString("page")) : 0;
        int size = params.has("size") ? Integer.parseInt(params.getString("size")) : 0;
        String order = params.has("order") ? params.getString("order") : "";
        String like = params.has("lks") ? params.getString("lks") : "";

        Iterator<String> ks = params.keys();
        while( ks.hasNext()){
            String key = ks.next();
            String value = params.getString(key);
            if(where != "")
                where += " and ";

                if(is_search){
                    where += key + " like ? ";
                    values.put("%" + value + "%");
                }else{
                    where += key + " = ? ";
                    values.put(value);
                }
        }

        if(tablename == "QuerySqlSelect"){
            sql += "";
        }else{
            sql = "SELECT * FROM " + tablename;
            if(where != ""){
                sql += " WHERE " + where;
            }
        }

        return execQuery(sql, values);
    }

    private static JSONObject execQuery(String sql, JSONArray values){
        Boolean flag = true;
        int errorCode = 0;
        String errorMessage = "";
        Connection conn = createConnection();
        if(conn == null)
            return null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        JSONArray json = new JSONArray();
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
            if(stmt.execute()){
                rs =  stmt.getResultSet();
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                while(rs.next()){
                    JSONObject jo = new JSONObject();
                    for (int i = 1; i <= columnCount; i++) {
                        String theKey = rsmd.getColumnLabel(i);
                        if(theKey.endsWith("_json")){
                            String theValue = rs.getObject(i) == null ? null : rs.getObject(i).toString();
                            if(theValue != null && theValue.startsWith("["))
                                jo.put(theKey, new JSONArray(theValue));
                            else if(theValue != null)
                                jo.put(theKey, new JSONObject(theValue));
                        }else{
                            jo.put(theKey, rs.getObject(i));
                        }
                    }
                    json.put(jo);
                }
                System.out.println("SQL: " + sql + "; VALUES: " + values.toString());
            }
        }
        catch (SQLException ex){
            flag = false;
            errorCode = ex.getErrorCode();
            errorMessage = ex.getMessage();
            System.out.println("SQL: " + sql + "; Code: " + ex.getErrorCode() + "; Message: " + ex.getMessage());
        }
        finally {
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
        JSONObject result = new JSONObject();
        if(flag){
            result.put("code", 200);
            result.put("rows", json);
        }else{
            result.put("code", 500);
            result.put("errcode", errorCode);
            result.put("message", errorMessage);
        }
        return result;
    }
}

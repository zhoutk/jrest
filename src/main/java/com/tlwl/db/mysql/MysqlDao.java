package com.tlwl.db.mysql;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import com.tlwl.utils.Tools;

public class MysqlDao {
    private static Connection createConnection(){
        Connection conn = null;
        try {
            JSONObject configs = Tools.jsonRead("./configs.json");
            JSONObject dbConfs = configs.getJSONObject("db_mysql_config");
            conn = DriverManager.getConnection(
                    "jdbc:mysql://"+dbConfs.getString("db_host")+":"+dbConfs.getInt("db_port")+"/"+dbConfs.getString("db_name")+"?"+
                            "user="+dbConfs.getString("db_user")+"&password="+dbConfs.getString("db_passwd")+"&characterEncoding=utf8&useSSL=false&autoReconnect=true");
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return conn;
    }

    public static JSONObject select(String tablename, JSONObject params, String [] fields){  //String tablename, Object params, String [] fields
        Connection conn = createConnection();
        if(conn == null)
            return null;
        Statement stmt = null;
        ResultSet rs = null;
        JSONArray json = new JSONArray();
        try {
            stmt = conn.createStatement();
            if(stmt.execute("SELECT * FROM " + tablename)){
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
            }
        }
        catch (Exception ex){
            System.out.println("SQLException: " + ex.getMessage());
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
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                }
            }
        }
        JSONObject result = new JSONObject();
        result.put("code", 200);
        result.put("rows", json);
        return result;
    }
}

package com.tlwl.db.mysql;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class DbHelper {
    private static Connection createConnection(){
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://192.168.1.8:3306/jrest?"+
                            "user=root&password=123456&characterEncoding=utf8&useSSL=false&autoReconnect=true");
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return conn;
    }

    public static JSONObject select(String id){  //String tablename, Object params, String [] fields
        Connection conn = createConnection();
        if(conn == null)
            return null;
        Statement stmt = null;
        ResultSet rs = null;
        JSONArray json = new JSONArray();
        try {
            stmt = conn.createStatement();
            if(stmt.execute("SELECT * FROM " + id)){
                rs =  stmt.getResultSet();
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                while(rs.next()){
                    JSONObject jo = new JSONObject();
                    for (int i = 1; i <= columnCount; i++) {
                        String theKey = rsmd.getColumnLabel(i);
                        if(theKey.endsWith("_json")){
                            String theValue = rs.getObject(i).toString();
                            if(theValue.startsWith("["))
                                jo.put(theKey, new JSONArray(theValue));
                            else
                                jo.put(theKey, new JSONObject(theValue));
                        }else{
                            jo.put(theKey, rs.getObject(i));
                        }
                    }
                    json.put(jo);
                }
            }
        }
        catch (SQLException ex){
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
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

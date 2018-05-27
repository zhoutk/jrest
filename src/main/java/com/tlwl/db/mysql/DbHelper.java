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
                    "jdbc:mysql://tlwl2020.mysql.rds.aliyuncs.com:3686/jrest?"+
                            "user=root&password=znhl2017UP&characterEncoding=utf8&useSSL=false");
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
                        jo.put(rsmd.getColumnLabel(i), rs.getObject(i));
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
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlEx) { } // ignore

                conn = null;
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { } // ignore

                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) { } // ignore

                stmt = null;
            }
        }
        JSONObject result = new JSONObject();
        result.put("code", 200);
        result.put("rows", json);
        return result;
    }
}

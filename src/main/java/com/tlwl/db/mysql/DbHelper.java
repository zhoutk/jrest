package com.tlwl.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static List<Object> select(){  //String tablename, Object params, String [] fields
        Connection conn = createConnection();
        if(conn == null)
            return null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Object> list = new ArrayList();
        try {
            stmt = conn.createStatement();
            if(stmt.execute("SELECT * FROM role")){
                rs =  stmt.getResultSet();
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                while(rs.next()){
                    Map<String, Object> map = new HashMap();
                    for (int i = 1; i <= columnCount; i++) {
                        map.put(rsmd.getColumnLabel(i), rs.getObject(i));
                    }
                    list.add(map);
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
        return list;
    }
}

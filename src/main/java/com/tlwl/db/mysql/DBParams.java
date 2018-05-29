package com.tlwl.db.mysql;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBParams
{
    private static final int[] SQLTYPES = {Types.INTEGER, Types.BIGINT, Types.VARCHAR, Types.DATE, Types.TIMESTAMP, Types.DOUBLE, Types.TIME};
    private static final Class<?>[] CLASSTYPES = {Integer.class, Long.class, String.class, Date.class, Timestamp.class, Double.class, Time.class};
    private List<ParamEntity> paramList = new ArrayList<>();

    public void addParam(Object o){
        addParam(o, getSqlTypeByClassType(o.getClass()));
    }

    private void addParam(Object o, int type){
        ParamEntity entity = new ParamEntity(o, type);
        paramList.add(entity);
    }

    private int getSqlTypeByClassType(Type type){
        for(int i = 0; i < CLASSTYPES.length; i++){
            if(type == CLASSTYPES[i]){
                return SQLTYPES[i];
            }
        }
        throw new RuntimeException("unSupport Type type:" + type);
    }

    private int checkSupportType(int sqlType){
        for(int i = 0; i < SQLTYPES.length; i++){
            if(sqlType == SQLTYPES[i]){
                return i;
            }
        }
        throw new RuntimeException("unsurpport sqltype:" + sqlType);
    }

    public void addNullParam(int sqlType){
        checkSupportType(sqlType);
        addParam(null, sqlType);
    }

    public void addNullParam(Type t){
        addParam(null, getSqlTypeByClassType(t));
    }

    public void prepareStatement(PreparedStatement pst,int startIndex) throws SQLException{
        for(ParamEntity e: paramList){
            int v = e.getValueType();
            Object o = e.getValue();
            if(o == null){
                pst.setNull(startIndex++, v);
                continue;
            }

            if(v == SQLTYPES[0]){
                pst.setInt(startIndex++, (Integer) o);
            }else if(v == SQLTYPES[1]){
                pst.setLong(startIndex++, (Long) o);
            }else if(v == SQLTYPES[2]){
                pst.setString(startIndex++, (String) o);
            }else if(v == SQLTYPES[3]){
                pst.setDate(startIndex++, (Date) o);
            }else if(v == SQLTYPES[4]){
                pst.setTimestamp(startIndex++, (Timestamp) o);
            }else if(v == SQLTYPES[5]){
                pst.setDouble(startIndex++, (Double) o);
            }else if(v == SQLTYPES[6]){
                pst.setTime(startIndex++, (Time) o);
            }
        }
    }

    public void prepareStatement(PreparedStatement pst) throws SQLException{
        prepareStatement(pst, 1);
    }
}

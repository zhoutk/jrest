package com.tlwl.db.mysql;

public class ParamEntity {
    private Object value;
    private int sqlType;

    public ParamEntity(Object value, int sqlType){
        this.value = value;
        this.sqlType = sqlType;
    }

    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    public int getValueType() {
        return sqlType;
    }
    public void setValueType(int sqlType) {
        this.sqlType = sqlType;

    }
}
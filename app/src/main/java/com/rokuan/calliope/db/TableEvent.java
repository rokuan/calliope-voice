package com.rokuan.calliope.db;

/**
 * Created by LEBEAU Christophe on 15/06/15.
 */
public class TableEvent {
    private String name;

    public TableEvent(String tableName){
        name = tableName;
    }

    public String getMessage(){
        return name;
    }
}

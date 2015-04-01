package com.rokuan.calliope.file;

/**
 * Created by LEBEAU Christophe on 01/04/2015.
 */
public abstract class SourceObject {
    public enum ObjectType {
        TEXT,
        IMAGE,
        AUDIO,
        VIDEO,
        LINK
    }

    private ObjectType type;

    protected SourceObject(ObjectType ty){
        type = ty;
    }

    public ObjectType getType(){
        return type;
    }
}

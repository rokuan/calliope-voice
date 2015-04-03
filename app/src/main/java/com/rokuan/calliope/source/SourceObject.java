package com.rokuan.calliope.source;

import android.content.Context;

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

    private Context context;
    private ObjectType type;

    protected SourceObject(ObjectType ty){
        type = ty;
    }

    protected SourceObject(ObjectType ty, Context c){
        type = ty;
        context = c;
    }

    public ObjectType getType(){
        return type;
    }
    public Context getContext() { return context; };
}

package com.rokuan.calliope.modules;

import android.content.Context;

/**
 * Created by LEBEAU Christophe on 24/03/2015.
 */
public abstract class IntentModule implements CalliopeModule {
    private Context context;

    public IntentModule(Context c){
        context = c;
    }

    public final Context getContext(){
        return context;
    }
}

package com.rokuan.calliope.modules;


import com.rokuan.calliope.HomeActivity;

/**
 * Created by LEBEAU Christophe on 24/03/2015.
 */
public abstract class CalliopeModule implements InterpretationModule {
    private HomeActivity activity;

    public CalliopeModule(HomeActivity a){
        activity = a;
    }

    public final HomeActivity getActivity(){
        return activity;
    }
}

package com.rokuan.calliope.modules;

import com.rokuan.calliope.HomeActivity;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;

/**
 * Created by LEBEAU Christophe on 30/04/2015.
 */
public class SystemModule extends CalliopeModule {
    public SystemModule(HomeActivity a) {
        super(a);
    }

    @Override
    public boolean canHandle(InterpretationObject object) {
        return false;
    }

    @Override
    public boolean submit(InterpretationObject object) {
        return false;
    }
}

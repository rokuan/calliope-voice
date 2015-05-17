package com.rokuan.calliope.modules;

import com.rokuan.calliope.HomeActivity;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;

/**
 * Created by Lebeau Lucie on 18/05/15.
 */
public class LanguageModule extends CalliopeModule {
    public LanguageModule(HomeActivity a) {
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

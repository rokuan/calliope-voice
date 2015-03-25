package com.rokuan.calliope.modules;

import com.rokuan.calliopecore.sentence.structure.InterpretationObject;

/**
 * Created by LEBEAU Christophe on 24/03/2015.
 */
public interface CalliopeModule {
    boolean canHandle(InterpretationObject object);
    boolean submit(InterpretationObject object);
}

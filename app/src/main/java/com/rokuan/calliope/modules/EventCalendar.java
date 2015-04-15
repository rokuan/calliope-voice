package com.rokuan.calliope.modules;

import android.content.Context;

import com.rokuan.calliopecore.sentence.structure.InterpretationObject;

/**
 * Created by LEBEAU Christophe on 14/04/2015.
 */
public class EventCalendar extends IntentModule {
    public EventCalendar(Context c) {
        super(c);
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

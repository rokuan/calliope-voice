package com.rokuan.calliope.modules;

import com.rokuan.calliopecore.sentence.structure.InterpretationObject;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class WeatherModule implements CalliopeModule {
    private static final String CONTENT_REGEX = "(météo|prévision(s?)|température(s?)|temps)";

    @Override
    public boolean canHandle(InterpretationObject object) {
        if(object.what != null){
            if(object.what.object.matches(CONTENT_REGEX)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean submit(InterpretationObject object) {
        // TODO:
        return false;
    }

}

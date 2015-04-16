package com.rokuan.calliope.modules;

import android.content.Context;

import com.rokuan.calliopecore.sentence.Action;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.QuestionObject;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class TVModule extends ContextModule {
    private static final String WHERE_CONTENT_REGEX = "(télé((vision)?))";
    private static final String CONTENT_REGEX = "(émission|programme|chaîne)";

    public TVModule(Context c) {
        super(c);
    }

    @Override
    public boolean canHandle(InterpretationObject object) {
        /*if(object.what != null){
            if(object.what.object.matches(CONTENT_REGEX)){
                switch((Action.VerbAction)object.action){
                    case SHOW:
                    case DISPLAY_SHOW:
                    case GIVE__PASS:

                        return true;
                }
            }
        }

        return false;*/
        // TODO:
        return true;
    }

    @Override
    public boolean submit(InterpretationObject object) {
        if(object.getType() == InterpretationObject.RequestType.QUESTION){
            QuestionObject question = (QuestionObject)object;

            if(question.qType == QuestionObject.QuestionType.WHAT){
                //question.
                return true;
            }
        } else if(object.getType() == InterpretationObject.RequestType.ORDER){
            switch((Action.VerbAction)object.action){

            }
        }



        return false;
    }

    private void findListings(String channelName, int channelNumber){

    }
}

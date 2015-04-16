package com.rokuan.calliope.modules;

import android.content.Context;

import com.rokuan.calliopecore.sentence.Action;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;

/**
 * Created by LEBEAU Christophe on 15/04/2015.
 */
public class MessageModule extends ContextModule {
    private static final String SMS_CONTENT_REGEX = "(message|SMS|sms)";
    private static final String MAIL_CONTENT_REGEX = "(mail|e-mail)";
    private static final String CONTENT_REGEX = "(" + SMS_CONTENT_REGEX + "|" + MAIL_CONTENT_REGEX + ")";

    public MessageModule(Context c) {
        super(c);
    }

    @Override
    public boolean canHandle(InterpretationObject object) {
        return object.getDescription().matches(".*:" + CONTENT_REGEX);
    }

    @Override
    public boolean submit(InterpretationObject object) {
        if(object.what != null){
            if(object.what.object.matches(SMS_CONTENT_REGEX)){
                switch((Action.VerbAction)object.action){
                    case CREATE_AGAIN:
                    case MAKE:
                    case MAKE_AGAIN:
                    case WRITE:
                    case WRITE_AGAIN:
                    case COMPOSE:
                        // TODO:
                        return true;
                }
            } else if(object.what.object.matches(MAIL_CONTENT_REGEX)) {

            }
        }
        return false;
    }
}

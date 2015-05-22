package com.rokuan.calliope.modules;

import android.content.Context;

import com.rokuan.calliope.HomeActivity;
import com.rokuan.calliopecore.sentence.Action;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.nominal.ComplementObject;
import com.rokuan.calliopecore.sentence.structure.nominal.NominalGroup;

/**
 * Created by LEBEAU Christophe on 15/04/2015.
 */
public class MessageModule extends CalliopeModule {
    private static final String SMS_CONTENT_REGEX = "(message|SMS|sms)";
    private static final String MAIL_CONTENT_REGEX = "(mail|e-mail)";
    private static final String CONTENT_REGEX = "(" + SMS_CONTENT_REGEX + "|" + MAIL_CONTENT_REGEX + ")";

    public MessageModule(HomeActivity a) {
        super(a);
    }

    @Override
    public boolean canHandle(InterpretationObject object) {
        return object.getDescription().matches(".*:" + CONTENT_REGEX);
    }

    @Override
    public boolean submit(InterpretationObject object) {
        if(object.what != null && object.what.getType() == NominalGroup.GroupType.COMPLEMENT){
            ComplementObject compl = (ComplementObject)object.what;

            if(compl.object.matches(SMS_CONTENT_REGEX)){
                switch((Action.VerbAction)object.action){
                    case CREATE_AGAIN:
                    case MAKE:
                    case MAKE_AGAIN:
                    case WRITE:
                    case WRITE_AGAIN:
                    case COMPOSE:
                        // TODO:
                        return true;

                    case SEND:

                        return true;
                }
            } else if(compl.object.matches(MAIL_CONTENT_REGEX)) {

            }
        }

        return false;
    }
}

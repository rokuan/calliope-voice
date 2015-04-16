package com.rokuan.calliope.modules;

import android.content.Context;

import com.rokuan.calliopecore.sentence.Action;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.OrderObject;
import com.rokuan.calliopecore.sentence.structure.QuestionObject;

/**
 * Created by LEBEAU Christophe on 25/03/2015.
 */
public class ContactModule extends ContextModule {
    private static final String CONTENT_REGEX = "(contact(s?))";
    private static final String CONTACT_INFO_REGEX = "(information(s?))";
    private static final String CONTACT_ATTRIBUTE_REGEX = "(numéro|nom|prénom|adresse|(e?)mail)";
    private static final String CONTACT_REGEX = "(" + CONTENT_REGEX + " | "+ CONTACT_INFO_REGEX +" | " + CONTACT_ATTRIBUTE_REGEX + ")";

    public ContactModule(Context c) {
        super(c);
    }

    @Override
    public boolean canHandle(InterpretationObject object) {
        if(object.what != null && object.what.object != null && object.what.object.matches(CONTACT_REGEX)){
            return true;
        }

        return false;
    }

    @Override
    public boolean submit(InterpretationObject object) {
        if(object.getType() == InterpretationObject.RequestType.QUESTION){
            QuestionObject question = (QuestionObject)object;

            if(question.qType == QuestionObject.QuestionType.WHAT && question.action == Action.VerbAction.BE){
                OrderObject order = new OrderObject();

                order.action = Action.VerbAction.GIVE;
                order.what = question.what;

                return submit(order);
            }
        } else if(object.getType() == InterpretationObject.RequestType.ORDER){
            if(object.what != null && object.what.object != null){
                    if(object.what.object.matches(CONTACT_INFO_REGEX)){
                        switch((Action.VerbAction)object.action){
                            case GIVE:
                            case DISPLAY:
                            case TAKE__PICK:
                            case TAKE:
                                case DISPLAY_SHOW:
                                    // TODO:
                                    return true;

                        }
                    } else if(object.what.object.matches(CONTACT_ATTRIBUTE_REGEX)){

                    }
            }
        }


        return false;
    }
}

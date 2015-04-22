package com.rokuan.calliope.modules;

import android.content.Context;

import com.rokuan.calliope.HomeActivity;
import com.rokuan.calliopecore.sentence.Action;
import com.rokuan.calliopecore.sentence.structure.ComplementObject;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.NominalGroup;
import com.rokuan.calliopecore.sentence.structure.OrderObject;
import com.rokuan.calliopecore.sentence.structure.QuestionObject;

/**
 * Created by LEBEAU Christophe on 25/03/2015.
 */
public class ContactModule extends CalliopeModule {
    private static final String CONTENT_REGEX = "(contact(s?))";
    private static final String CONTACT_INFO_REGEX = "(information(s?))";
    private static final String CONTACT_ATTRIBUTE_REGEX = "(numéro|nom|prénom|adresse|(e?)mail)";
    private static final String CONTACT_REGEX = "(" + CONTENT_REGEX + " | "+ CONTACT_INFO_REGEX +" | " + CONTACT_ATTRIBUTE_REGEX + ")";

    public ContactModule(HomeActivity a) {
        super(a);
    }

    @Override
    public boolean canHandle(InterpretationObject object) {
        if(object.what != null){
            if(object.what.getType() == NominalGroup.GroupType.NOMINAL_GROUP) {
                ComplementObject compl = (ComplementObject) object.what;
                return compl.object != null && compl.object.matches(CONTACT_REGEX);
            }

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
            if(object.what != null && object.what.getType() == NominalGroup.GroupType.NOMINAL_GROUP){
                ComplementObject compl = (ComplementObject)object.what;

                if(compl.object != null) {
                    if (compl.object.matches(CONTACT_INFO_REGEX)) {
                        switch ((Action.VerbAction) object.action) {
                            case GIVE:
                            case DISPLAY:
                            case TAKE__PICK:
                            case TAKE:
                            case DISPLAY_SHOW:
                                // TODO:
                                return true;

                        }
                    } else if (compl.object.matches(CONTACT_ATTRIBUTE_REGEX)) {

                    }
                }
            }
        }


        return false;
    }
}

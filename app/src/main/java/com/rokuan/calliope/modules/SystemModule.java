package com.rokuan.calliope.modules;

import com.rokuan.calliope.HomeActivity;
import com.rokuan.calliope.source.TextSource;
import com.rokuan.calliopecore.sentence.Action;
import com.rokuan.calliopecore.sentence.structure.ComplementObject;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.NominalGroup;
import com.rokuan.calliopecore.sentence.structure.QuestionObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by LEBEAU Christophe on 30/04/2015.
 */
public class SystemModule extends CalliopeModule {
    public SystemModule(HomeActivity a) {
        super(a);
    }

    @Override
    public boolean canHandle(InterpretationObject object) {
        if(object.what != null && object.what.getType() == NominalGroup.GroupType.NOMINAL_GROUP){
            ComplementObject compl = (ComplementObject)object.what;

            if(compl.object.matches("heure")){

            } else if(compl.object.matches("jour")) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean submit(InterpretationObject object) {
        if(object.what != null && object.what.getType() == NominalGroup.GroupType.NOMINAL_GROUP){
            ComplementObject compl = (ComplementObject)object.what;

            if(compl.object.matches("heure")){
                if(object.getType() == InterpretationObject.RequestType.QUESTION){
                    QuestionObject qObject = (QuestionObject)object;

                    if(qObject.qType == QuestionObject.QuestionType.WHAT && qObject.action == Action.VerbAction.BE){
                        displayTime(object);
                    }
                }
            } else if(compl.object.matches("jour")) {
                if(object.getType() == InterpretationObject.RequestType.QUESTION){
                    QuestionObject qObject = (QuestionObject)object;

                    if(qObject.qType == QuestionObject.QuestionType.WHAT && qObject.action == Action.VerbAction.BE){
                        displayDate(object);
                        return true;
                    }
                }

                return false;
            }
        }

        return false;
    }

    private void displayDate(InterpretationObject object){
        if(object.where == null){
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();
            String dateText = new SimpleDateFormat("cccc d MMMM y").format(currentDate);
            this.getActivity().addSource(new TextSource(dateText));
        } else {
            // TODO: donner la date de la ville si le where est une ville (decalage horaire)
        }
    }

    private void displayTime(InterpretationObject object){
        if(object.where == null){
            //String timeText = "Il est ";
            Calendar calendar = Calendar.getInstance();
            Date currentTime = calendar.getTime();
            String timeText = new SimpleDateFormat("HH:mm").format(currentTime);
            this.getActivity().addSource(new TextSource(timeText));
        } else {
            // TODO: donner l'heure de la ville si le where est une ville
        }
    }
}

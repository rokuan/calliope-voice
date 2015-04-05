package com.rokuan.calliope.modules;

import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;

import com.rokuan.calliope.HomeActivity;
import com.rokuan.calliopecore.sentence.Action;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.data.time.SingleTimeObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by LEBEAU Christophe on 05/04/2015.
 */
public class AlarmHandler extends IntentModule {
    private static final String CONTENT_ALARM_REGEX = "(alarme)";
    private static final String CONTENT_TIMER_REGEX = "(minuteur|chronomètre|compteur)";
    private static final String CONTENT_REGEX = CONTENT_ALARM_REGEX + "|" + CONTENT_TIMER_REGEX;

    public AlarmHandler(Context c) {
        super(c);
    }

    @Override
    public boolean canHandle(InterpretationObject object) {
        if(object.action == Action.VerbAction.ALERT){
            return true;
        }

        if(object.what != null && object.what.object != null && object.what.object.matches(CONTENT_REGEX)){
            return true;
        }

        return false;
    }

    @Override
    public boolean submit(InterpretationObject object) {
        switch((Action.VerbAction)object.action){
            case ALERT:
            case WAKE_UP:
            case NOTIFY:
                if(object.what.when != null){
                    switch(object.what.when.getType()){
                        case SINGLE:
                            SingleTimeObject time = (SingleTimeObject)object.what.when;
                            setNewAlarm("Calliope", time.date);
                            break;
                    }
                }

                // TODO: que faire si l'heure n'est pas precisee ou si ce n'est pas une date simple
                return true;
        }

        if(object.what != null && object.what.object != null){
            if(object.what.object.matches(CONTENT_ALARM_REGEX)){

            } else if(object.what.object.matches(CONTENT_TIMER_REGEX)){

            }
        }

        return false;
    }

    public void setNewAlarm(String alarmTitle, Date time){
        Calendar calendar = Calendar.getInstance();
        int hour = 0;
        int minutes = 0;

        calendar.setTime(time);
        hour = calendar.get(Calendar.HOUR);
        minutes = calendar.get(Calendar.MINUTE);

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, alarmTitle)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes);

        if (intent.resolveActivity(this.getContext().getPackageManager()) != null) {
            this.getContext().startActivity(intent);
        }
    }
}

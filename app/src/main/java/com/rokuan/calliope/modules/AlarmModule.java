package com.rokuan.calliope.modules;

import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.util.Log;

import com.rokuan.calliope.HomeActivity;
import com.rokuan.calliope.source.AlarmSource;
import com.rokuan.calliope.view.AlarmView;
import com.rokuan.calliopecore.sentence.Action;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.data.time.SingleTimeObject;
import com.rokuan.calliopecore.sentence.structure.nominal.ComplementObject;
import com.rokuan.calliopecore.sentence.structure.nominal.NominalGroup;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by LEBEAU Christophe on 05/04/2015.
 */
public class AlarmModule extends CalliopeModule {
    private static final String CONTENT_ALARM_REGEX = "(alarme)";
    private static final String CONTENT_TIMER_REGEX = "(minuteur|chronomètre|compteur|timer)";
    private static final String CONTENT_REGEX = CONTENT_ALARM_REGEX + "|" + CONTENT_TIMER_REGEX;

    public AlarmModule(HomeActivity a) {
        super(a);
    }

    @Override
    public boolean canHandle(InterpretationObject object) {
        switch((Action.VerbAction)object.action){
            case ALERT:
            case NOTIFY:
            case WAKE_UP:
                return true;
        }

        if(object.what != null && object.what.getType() == NominalGroup.GroupType.COMPLEMENT){
            return ((ComplementObject)object.what).object.matches(CONTENT_REGEX);
        }

        return false;
    }

    @Override
    public boolean submit(InterpretationObject object) {
        switch((Action.VerbAction)object.action){
            case ALERT:
            case WAKE_UP:
            case NOTIFY:
                Date time = getTimeIfExists(object);

                if(time == null){
                    // TODO: que faire si l'heure n'est pas precisee ou si ce n'est pas une date simple
                    return false;
                }

                setNewAlarm("Calliope", time);
                return true;
        }

        if(object.what != null && object.what.getType() == NominalGroup.GroupType.COMPLEMENT){
            ComplementObject compl = (ComplementObject)object.what;

            if(compl.object.matches(CONTENT_ALARM_REGEX)){
                switch((Action.VerbAction)object.action){
                    case CREATE_AGAIN:
                    case MAKE:
                    case SAVE__RECORD:
                    case ADD:
                        Date time = getTimeIfExists(object);

                        if(time == null){
                            return false;
                        }

                        setNewAlarm("Calliope - Alarm", time);
                        return true;
                }
            } else if(compl.object.matches(CONTENT_TIMER_REGEX)){
                switch((Action.VerbAction)object.action){
                    case START:
                    case ACTIVATE:
                    case OPEN:
                        Date time = getTimeIfExists(object);

                        if(time == null){
                            return false;
                        }

                        startTimer("Calliope - Timer", getSecondsFromTime(time));
                        return true;
                }
            }
        }

        return false;
    }

    private Date getTimeIfExists(InterpretationObject object){
        /*if(object.what == null){

        }
        if(object.what.when != null){
            switch(object.what.when.getType()){
                case SINGLE:
                    SingleTimeObject time = (SingleTimeObject)object.what.when;
                    return time.date;
            }
        }*/
        if(object.when != null){
            switch(object.when.getType()){
                case SINGLE:
                    SingleTimeObject time = (SingleTimeObject)object.when;
                    return time.date;
            }
        }

        return null;
    }

    private int getSecondsFromTime(Date time){
        Calendar calendar = Calendar.getInstance();
        int seconds = 0;

        calendar.setTime(time);
        seconds += calendar.get(Calendar.SECOND);
        seconds += calendar.get(Calendar.MINUTE) * 60;
        seconds += calendar.get(Calendar.HOUR_OF_DAY) * 3600;

        return seconds;
    }

    public void setNewAlarm(String alarmTitle, Date time){
        Calendar calendar = Calendar.getInstance();
        int hour;
        int minutes;

        calendar.setTime(time);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, alarmTitle)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);

        if (intent.resolveActivity(this.getActivity().getPackageManager()) != null) {
            this.getActivity().startActivity(intent);

            /*AlarmView aView = new AlarmView(this.getActivity(), time);
            this.getActivity().insertView(aView);*/
            this.getActivity().addSource(new AlarmSource(time));
        }
    }

    public void startTimer(String message, int seconds) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_LENGTH, seconds)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);

        if (intent.resolveActivity(this.getActivity().getPackageManager()) != null) {
            this.getActivity().startActivity(intent);
        }
    }
}

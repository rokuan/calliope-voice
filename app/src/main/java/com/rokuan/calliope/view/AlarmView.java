package com.rokuan.calliope.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rokuan.calliope.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LEBEAU Christophe on 13/04/2015.
 */
public class AlarmView extends LinearLayout {
    private Date time;

    public AlarmView(Context context, Date alarmTime) {
        super(context);
        time = alarmTime;
        initAlarmView();
    }

    private void initAlarmView(){
        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.view_alarm, this);

        TextView timeView = (TextView)this.findViewById(R.id.view_alarm_time);
        timeView.setText(new SimpleDateFormat("HH:mm").format(time));
    }
}

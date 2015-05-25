package com.rokuan.calliope.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rokuan.calliope.R;
import com.rokuan.calliope.receiver.SmsData;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by LEBEAU Christophe on 25/05/15.
 */
public class SmsView extends LinearLayout {
    private SmsData data;

    @InjectView(R.id.view_sms_targets) protected TextView fromOrTo;
    @InjectView(R.id.view_sms_content) protected TextView messageContent;

    public SmsView(Context context, SmsData d) {
        super(context);
        data = d;

        initSmsView();
    }

    private void initSmsView(){
        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        inflater.inflate(R.layout.view_sms, this);

        ButterKnife.inject(this);

        if(data.getSender() != null){
            fromOrTo.setText(data.getSender());
        } else {
            String receiversListString = data.getReceiver().toString();
            receiversListString.substring(1, receiversListString.length() - 1).trim();
            fromOrTo.setText(receiversListString);
        }

        messageContent.setText(data.getContent());
    }
}

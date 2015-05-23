package com.rokuan.calliope.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rokuan.calliope.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by LEBEAU Christophe on 23/05/15.
 */
public class CommandView extends LinearLayout {
    private String command;

    @InjectView(R.id.view_command_content) protected TextView contentView;

    public CommandView(Context context, String cmd) {
        super(context);
        command = cmd;

        initCommandView();
    }

    private void initCommandView(){
        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.view_command, this);
        ButterKnife.inject(this);
        contentView.setText(command);
    }
}

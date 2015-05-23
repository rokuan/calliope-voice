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
public class StringView extends LinearLayout {
    private String text;

    @InjectView(R.id.view_string_content) protected TextView contentView;

    public StringView(Context context, String t) {
        super(context);
        text = t;

        initStringView();
    }

    private void initStringView(){
        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.view_string, this);
        ButterKnife.inject(this);
        contentView.setText(text);
    }
}

package com.rokuan.calliope.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rokuan.calliope.R;
import com.rokuan.calliope.api.google.TranslationData;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Lebeau Lucie on 22/05/15.
 */
public class TranslationView extends LinearLayout {
    private TranslationData data;

    @InjectView(R.id.view_translation_languages) protected TextView languagesView;
    @InjectView(R.id.view_translation_content) protected TextView translatedTextView;

    public TranslationView(Context context, TranslationData d) {
        super(context);
        data = d;

        initTranslationView();
    }

    private void initTranslationView(){
        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.view_translation, this);
        ButterKnife.inject(this);

        languagesView.setText(data.getSourceLanguage().toUpperCase() + " > " + data.getTargetLanguage().toUpperCase());
        translatedTextView.setText(data.getTranslatedText());
    }
}

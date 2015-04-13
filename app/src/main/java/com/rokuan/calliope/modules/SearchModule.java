package com.rokuan.calliope.modules;

import android.content.Context;

import com.rokuan.calliopecore.sentence.structure.InterpretationObject;

/**
 * Created by LEBEAU Christophe on 12/04/2015.
 */
public class SearchModule extends IntentModule {
    private static final String ITEM_CONTENT_REGEX = "((vidéo|chanson|musique|image)(s?))";

    public SearchModule(Context c) {
        super(c);
    }

    @Override
    public boolean canHandle(InterpretationObject object) {
        return false;
    }

    @Override
    public boolean submit(InterpretationObject object) {
        return false;
    }
}

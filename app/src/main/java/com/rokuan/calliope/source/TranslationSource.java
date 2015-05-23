package com.rokuan.calliope.source;

import com.rokuan.calliope.api.google.TranslationData;

/**
 * Created by Lebeau Lucie on 22/05/15.
 */
public class TranslationSource extends SourceObject {
    private TranslationData data;

    public TranslationSource(TranslationData d) {
        super(ObjectType.TRANSLATION);
        data = d;
    }

    public TranslationData getTranslationData() {
        return data;
    }
}

package com.rokuan.calliope.source;

import com.rokuan.calliope.api.microsoft.TranslationData;

/**
 * Created by LEBEAU Christophe on 22/05/15.
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

package com.rokuan.calliope.source;

import com.rokuan.calliope.extract.TextExtractionListener;
import com.rokuan.calliope.extract.TextExtractor;

/**
 * Created by LEBEAU Christophe on 01/04/2015.
 */
public class TextSource extends SourceObject implements TextExtractor {
    private String text;

    public TextSource(String t) {
        super(ObjectType.TEXT);
        text = t;
    }


    @Override
    public String getText() {
        return text;
    }

    @Override
    public void getTextAsync(TextExtractionListener listener) {
        if(listener != null){
            listener.onExtractionStarted("");
            listener.onExtractionEnded(text);
        }
    }
}

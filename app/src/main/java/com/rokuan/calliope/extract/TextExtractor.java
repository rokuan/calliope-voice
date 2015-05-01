package com.rokuan.calliope.extract;

/**
 * Created by LEBEAU Christophe on 02/04/2015.
 */
public interface TextExtractor {
    String getText();
    void getTextAsync(TextExtractionListener listener);
}

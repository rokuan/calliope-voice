package com.rokuan.calliope.extract;

/**
 * Created by LEBEAU Christophe on 02/04/2015.
 */
public interface TextExtractor {
    public String getText();
    public void getTextAsync(TextExtractionListener listener);
}

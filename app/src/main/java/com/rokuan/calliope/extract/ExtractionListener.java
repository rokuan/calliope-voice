package com.rokuan.calliope.extract;

/**
 * Created by LEBEAU Christophe on 04/04/2015.
 */
public interface ExtractionListener<ReturnType> {
    void onExtractionStarted(String message);
    void onExtractionEnded(ReturnType value);
}

package com.rokuan.calliope.api;

/**
 * Created by LEBEAU Christophe on 27/04/2015.
 */
public interface ResultCallback<T> {
    void onResult(boolean success, T result);
}

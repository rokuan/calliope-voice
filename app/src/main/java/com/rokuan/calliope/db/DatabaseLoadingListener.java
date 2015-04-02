package com.rokuan.calliope.db;

/**
 * Created by LEBEAU Christophe on 02/04/2015.
 */
public interface DatabaseLoadingListener {
    void setOperationsCount(int count);
    //void onLoadingStarted();
    void onOperationStarted(int operationIndex, String message);
    void onOperationEnded(int operationIndex);
    //void onError(String error);
    //void onLoadingEnded();
}

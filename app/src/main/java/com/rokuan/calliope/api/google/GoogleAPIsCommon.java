package com.rokuan.calliope.api.google;

import android.content.Context;

import com.rokuan.calliope.R;

/**
 * Created by Lebeau Lucie on 22/05/15.
 */
public class GoogleAPIsCommon {
    public static String getAPIKey(Context context){
        return context.getResources().getString(R.string.google_apis_key);
    }
}

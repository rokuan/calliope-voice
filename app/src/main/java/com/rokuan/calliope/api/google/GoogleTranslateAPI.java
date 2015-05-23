package com.rokuan.calliope.api.google;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Lebeau Lucie on 22/05/15.
 */
public class GoogleTranslateAPI {
    private static final String TRANSLATE_API_ADDRESS = "https://www.googleapis.com/language/translate/v2?key=%s";

    public static String getTranslationURL(Context context, String text, String fromLang, @NonNull String toLang){
        String finalURL = String.format(TRANSLATE_API_ADDRESS, GoogleAPIsCommon.getAPIKey(context));

        if(fromLang != null){
            finalURL += "&source=" + fromLang;
        }

        finalURL += "&target=" + toLang;
        finalURL += "&q=" + text;

        return finalURL;
    }
}

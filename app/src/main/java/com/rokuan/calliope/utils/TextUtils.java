package com.rokuan.calliope.utils;

import android.support.annotation.NonNull;

import java.text.Normalizer;

/**
 * Created by LEBEAU Christophe on 30/04/2015.
 */
public class TextUtils {
    public static String removeAllAccents(@NonNull String s){
        String result = Normalizer.normalize(s, Normalizer.Form.NFD);
        result = result.replaceAll("[^\\p{ASCII}]", "");
        return result;
    }
}

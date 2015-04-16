package com.rokuan.calliope.view;

import android.content.Context;
import android.widget.LinearLayout;

import com.rokuan.calliope.api.openweather.ForecastData;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class ForecastView extends LinearLayout {
    private ForecastData data;

    public ForecastView(Context context, ForecastData fData) {
        super(context);
        data = fData;
        initForecastView();
    }

    private void initForecastView(){

    }
}

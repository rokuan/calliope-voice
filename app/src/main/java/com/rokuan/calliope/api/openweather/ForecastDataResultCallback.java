package com.rokuan.calliope.api.openweather;

import com.rokuan.calliope.api.ResultCallback;

/**
 * Created by LEBEAU Christophe on 27/04/2015.
 */
public interface ForecastDataResultCallback {
    void onForecastDataResult(boolean success, ForecastData result);
}

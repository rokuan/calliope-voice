package com.rokuan.calliope.source;

import com.rokuan.calliope.api.openweather.WeatherData;

/**
 * Created by LEBEAU Christophe on 29/04/2015.
 */
public class WeatherSource extends SourceObject {
    private WeatherData data;

    public WeatherSource(WeatherData d) {
        super(ObjectType.WEATHER);
        data = d;
    }

    public WeatherData getWeatherData(){
        return data;
    }
}

package com.rokuan.calliope.source;


import com.rokuan.calliope.api.openweather.OWMWeatherData;

/**
 * Created by LEBEAU Christophe on 29/04/2015.
 */
public class WeatherSource extends SourceObject {
    private OWMWeatherData data;

    public WeatherSource(OWMWeatherData d) {
        super(ObjectType.WEATHER);
        data = d;
    }

    public OWMWeatherData getWeatherData(){
        return data;
    }
}

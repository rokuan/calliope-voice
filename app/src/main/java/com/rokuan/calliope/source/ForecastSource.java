package com.rokuan.calliope.source;


import com.rokuan.calliope.api.darksky.ForecastData;

/**
 * Created by LEBEAU Christophe on 29/04/2015.
 */
public class ForecastSource extends SourceObject {
    private ForecastData data;

    public ForecastSource(ForecastData d) {
        super(ObjectType.FORECAST);
        data = d;
    }

    public ForecastData getForecastData(){
        return data;
    }
}

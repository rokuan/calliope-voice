package com.rokuan.calliope.api.openweather;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class City {
    private Long id;
    private String name;
    private String country;

    public City(long cityId, String cityName, String cityCountry){
        id = cityId;
        name = cityName;
        country = cityCountry;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }

        return (o instanceof City) && (((City)o).id == this.id);
    }

    @Override
    public String toString(){
        return this.name + "," + this.country;
    }
}

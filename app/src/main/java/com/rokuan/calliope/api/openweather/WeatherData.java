package com.rokuan.calliope.api.openweather;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class WeatherData {
    private City place;
    private double temperature;
    private double humidity;
    private double pressure;
    private double speed;
    private Date date;
    private Date sunrise;
    private Date sunset;
    private Bitmap weatherImage;
    private String weatherDescription;

    /**
     * Constructs an empty instance of WeatherData
     */
    protected WeatherData(){

    }

    /**
     * Builds a WeatherData object from the given JSONObject
     * @param json the JSONObject to build the object from
     * @return a new WeatherData instance with filled attributes
     * @throws JSONException
     */
    public static WeatherData buildFromJSON(JSONObject json) throws JSONException {
        WeatherData info = new WeatherData();

        JSONObject main = json.getJSONObject("main");
        JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
        JSONObject sys = json.getJSONObject("sys");

        long cityId = json.getLong("id");
        String cityName = json.getString("name");
        String cityCountry = sys.getString("country");

        info.place = new City(cityId, cityName, cityCountry);
        info.date = new Date(json.getLong("dt") * 1000);

        info.temperature = main.getDouble("temp");
        info.pressure = main.getDouble("pressure");
        info.humidity = main.getInt("humidity");

        info.speed = json.getJSONObject("wind").getInt("speed");

        info.weatherDescription = weather.getString("description");
        info.weatherImage = OpenWeatherMapAPI.getIcon(weather.getString("icon"));

        info.sunrise = new Date(sys.getLong("sunrise") * 1000);
        info.sunset = new Date(sys.getLong("sunset") * 1000);

        return info;
    }

    public double getTemperature() {
        // TODO: verifier l'unite de la temperature
        return (temperature - 273.15);
    }

    public double getHumidity() {
        return humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public Date getDate() {
        return date;
    }

    public Date getSunrise() {
        return sunrise;
    }

    public Date getSunset() {
        return sunset;
    }

    public Bitmap getWeatherImage() {
        return weatherImage;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public City getPlace() {
        return place;
    }

    /*public void setPlace(City place) {
        this.place = place;
    }*/

    public double getSpeed() {
        return speed;
    }
}

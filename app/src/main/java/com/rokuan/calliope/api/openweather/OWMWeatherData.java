package com.rokuan.calliope.api.openweather;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class OWMWeatherData {
    private City place;
    private double temperature;
    private double minTemperature;
    private double maxTemperature;
    private double humidity;
    private double pressure;
    private double speed;
    private Date date;
    private Date sunrise;
    private Date sunset;
    //private Bitmap weatherImage;
    private String weatherIconName;
    private String weatherDescription;

    /**
     * Constructs an empty instance of WeatherData
     */
    protected OWMWeatherData(){

    }

    /**
     * Builds a WeatherData object from the given JSONObject
     * @param json the JSONObject to build the object from
     * @return a new WeatherData instance with filled attributes
     * @throws JSONException
     */
    public static OWMWeatherData buildFromJSON(Context context, JSONObject json) throws JSONException {
        OWMWeatherData info = new OWMWeatherData();

        JSONObject main = json.getJSONObject("main");
        JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
        JSONObject sys = json.getJSONObject("sys");

        long cityId = json.getLong("id");
        String cityName = json.getString("name");
        String cityCountry = sys.getString("country");

        info.place = new City(cityId, cityName, cityCountry);
        info.date = new Date(json.getLong("dt") * 1000);

        info.temperature = main.getDouble("temp");
        info.minTemperature = main.getDouble("temp_min");
        info.maxTemperature = main.getDouble("temp_max");
        info.pressure = main.getDouble("pressure");
        info.humidity = main.getInt("humidity");

        info.speed = json.getJSONObject("wind").getInt("speed");

        info.weatherDescription = weather.getString("description");
        //info.weatherImage = OpenWeatherMapAPI.getIcon(weather.getString("icon"));
        info.weatherIconName = weather.getString("icon");
        /*try {
            info.weatherImage = Picasso.with(context).load(OpenWeatherMapAPI.getBitmapURL(weather.getString("icon"))).get();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        info.sunrise = new Date(sys.getLong("sunrise") * 1000);
        info.sunset = new Date(sys.getLong("sunset") * 1000);

        return info;
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

    /*public Bitmap getWeatherImage() {
        return weatherImage;
    }*/

    public String getIcon(){
        return weatherIconName;
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

    public double getTemperature() {
        // TODO: verifier l'unite de la temperature
        return (temperature - 273.15);
    }

    public double getMinTemperature() {
        return (minTemperature  - 273.15);
    }

    public double getMaxTemperature() {
        return (maxTemperature  - 273.15);
    }
}

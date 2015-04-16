package com.rokuan.calliope.api.openweather;

import android.content.Context;
import android.graphics.Bitmap;

import com.rokuan.calliope.utils.RemoteData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class ForecastData {
    static class SingleWeatherData {
        private static final int TEMP_DAY = 0;
        private static final int TEMP_MIN = 1;
        private static final int TEMP_MAX = 2;
        private static final int TEMP_NIGHT = 3;
        private static final int TEMP_EVENING = 4;
        private static final int TEMP_MORNING = 5;

        private Date date;
        private double[] temperatures; //"temp":{"day":9.23, "min":4.91, "max":12.25, "night":4.91, "eve":9.99, "morn":6.34},
        private double pressure; //"pressure":1024.93,
        private double humidity; //"humidity":95,
        private String weatherType;
        private String weatherDescription;
        private Bitmap weatherImage;
        private double speed;
        private double degree;
        private double clouds;
        private double rain;

        /**
         * Builds a new ForecastData from a given JSONObject
         * @param json the JSON data to build the item from
         * @return a ForecastData containing the required data found in {@code json}
         * @throws JSONException when a required field is not found in {@code json}
         */
        public static SingleWeatherData buildFromJSON(JSONObject json) throws JSONException {
            SingleWeatherData info = new SingleWeatherData();

            JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
            JSONObject temp = json.getJSONObject("temp");

            info.date = new Date(json.getLong("dt") * 1000);

            info.temperatures = new double[]{
                    temp.getDouble("day"),
                    temp.getDouble("min"),
                    temp.getDouble("max"),
                    temp.getDouble("night"),
                    temp.getDouble("eve"),
                    temp.getDouble("morn")
            };
            info.pressure = json.getDouble("pressure");
            info.humidity = json.getInt("humidity");

            info.weatherType = weather.getString("main");
            info.weatherDescription = weather.getString("description");
            info.weatherImage = OpenWeatherMapAPI.getIcon(weather.getString("icon"));

            info.speed = json.getDouble("speed");
            info.degree = json.getDouble("deg");
            info.clouds = json.getDouble("clouds");
            //info.rain = json.getDouble("rain");
            info.rain = 0;

            return info;
        }

        public Date getDate() {
            return date;
        }

        public double getPressure() {
            return pressure;
        }

        public double getHumidity() {
            return humidity;
        }

        public String getWeatherType() {
            return weatherType;
        }

        public String getWeatherDescription() {
            return weatherDescription;
        }

        public Bitmap getWeatherImage() {
            return weatherImage;
        }

        public double getSpeed() {
            return speed;
        }

        public double getDegree() {
            return degree;
        }

        public double getClouds() {
            return clouds;
        }

        public double getRain() {
            return rain;
        }

        public double getTemperature(){
            return (temperatures[TEMP_MAX] + temperatures[TEMP_MIN])/2;
        }

        public double getMinTemperature(){
            return temperatures[TEMP_MIN];
        }

        public double getMaxTemperature(){
            return temperatures[TEMP_MAX];
        }
    }

    private City city;
    private List<SingleWeatherData> forecast = new ArrayList<>();

    public static ForecastData buildFromJSON(JSONObject json){
        try {
            ForecastData data = new ForecastData();
            int resultsSize = json.getInt("cnt");
            JSONArray elements = json.getJSONArray("list");

            JSONObject cityObject = json.getJSONObject("city");
            long cityId = cityObject.getLong("id");
            String cityName = cityObject.getString("name");
            String cityCountry = cityObject.getString("country");

            data.city = new City(cityId, cityName, cityCountry);

            // TODO: verifier que cnt == count ?
            //if(elements.length() > 0) {
            if(resultsSize > 0){
                data.forecast = new ArrayList<SingleWeatherData>(resultsSize);

                for (int i=0; i<resultsSize; i++) {
                    JSONObject item = elements.getJSONObject(i);
                    data.forecast.add(SingleWeatherData.buildFromJSON(item));
                }
            }

            return data;
        } catch (JSONException e) {
            // TODO: erreur
            return null;
        }
    }
}

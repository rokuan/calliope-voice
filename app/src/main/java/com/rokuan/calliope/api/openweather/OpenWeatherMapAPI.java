package com.rokuan.calliope.api.openweather;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import com.rokuan.calliope.R;
import com.rokuan.calliope.utils.RemoteData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class OpenWeatherMapAPI {
    private static final String OPENWEATHER_API_ADDRESS = "http://api.openweathermap.org/data/2.5/";
    private static final String OPENWEATHER_ICON_ADDRESS = "http://openweathermap.org/img/w/%s.png";

    //private static final String PLACE_NAME_QUERY = OPENWEATHER_API_ADDRESS + "find?q=%s&type=like&cnt=4";
    /*private static final String WEATHER_QUERY = OPENWEATHER_API_ADDRESS + "weather?id=%s";
    private static final String FORECAST_QUERY = OPENWEATHER_API_ADDRESS + "forecast/daily?id=%s&units=metric&cnt=%d";*/
    private static final String WEATHER_QUERY = OPENWEATHER_API_ADDRESS + "weather?";
    private static final String FORECAST_QUERY = OPENWEATHER_API_ADDRESS + "forecast/daily?units=metric&cnt=%d";
    private static final String ICON_QUERY = OPENWEATHER_ICON_ADDRESS;

    private static final int ID_QUERY_TYPE = 0;
    private static final int LOCATION_QUERY_TYPE = 1;
    private static final int NAME_QUERY_TYPE = 2;

    private static String getApiKey(Context context){
        return context.getResources().getString(R.string.openweathermap_api_key);
    }

    //public static Bitmap getIcon(Context context, String iconName){
    public static Bitmap getIcon(String iconName){
        return RemoteData.getBitmapFromURL(String.format(ICON_QUERY, iconName));
    }

    /**
     * Returns the forecast for a specific city denoted by its ID for 7 days
     * @param context the android context to be used
     * @param placeId the place id
     * @return a forecast for the next 7 days or null if an error occurred
     */
    public static ForecastData getWeekForecastForId(Context context, long placeId){
        return getForecast(context, 7, ID_QUERY_TYPE, placeId, null, null);
    }

    public static ForecastData getWeekForecastForName(Context context, String placeName){
        return getForecast(context, 7, NAME_QUERY_TYPE, -1, placeName, null);
    }

    public static ForecastData getWeekForecastForLocation(Context context, Location placeLocation){
        return getForecast(context, 7, LOCATION_QUERY_TYPE, -1, null, placeLocation);
    }

    private static ForecastData getForecast(Context context, int count, int queryType, long placeId, String placeName, Location placeLocation){
        String query = addQueryParameter(String.format(FORECAST_QUERY, count), queryType, placeId, placeName, placeLocation);
        Log.i("Sunny", "Weather (Forecast query)" + query);
        JSONObject json = getJSON(context, query);
        return ForecastData.buildFromJSON(json);
    }

    /**
     * Gets a single weather data for the specified city
     * @param context the android context to be used
     * @param placeId the city id
     * @return a weather data or null if an error occurred
     */
    public static WeatherData getWeatherForId(Context context, long placeId){
        return getWeather(context, ID_QUERY_TYPE, placeId, null, null);
    }

    public static WeatherData getWeatherForName(Context context, String placeName){
        return getWeather(context, NAME_QUERY_TYPE, -1, placeName, null);
    }

    public static WeatherData getWeatherForLocation(Context context, Location placeLocation){
        return getWeather(context, LOCATION_QUERY_TYPE, -1, null, placeLocation);
    }

    private static WeatherData getWeather(Context context, int queryType, long placeId, String placeName, Location placeLocation){
        String query = addQueryParameter(WEATHER_QUERY, queryType, placeId, placeName, placeLocation);
        JSONObject result = getJSON(context, query);

        try {
            return WeatherData.buildFromJSON(result);
        } catch (JSONException e) {
            return null;
        }
    }

    private static String addQueryParameter(String initialQuery, int queryType, long placeId, String placeName, Location placeLocation){
        String query = initialQuery;

        switch(queryType){
            case ID_QUERY_TYPE:
                query += "id=" + placeId;
                break;

            case NAME_QUERY_TYPE:
                query += "q=" + placeName;
                break;

            case LOCATION_QUERY_TYPE:
                query += "lat=" + placeLocation.getLatitude() + "&lon=" + placeLocation.getLongitude();
                break;
        }

        return query;
    }

    /**
     * Gets a JSONObject from an http URL
     * @param context the android context to be used
     * @param address the http address to retrieve the JSON from
     * @return a json data from the given address or null if an error occurred
     */
    private static JSONObject getJSON(Context context, String address){
        ContentValues properties = new ContentValues();
        properties.put("x-api-key", getApiKey(context));
        JSONObject result = RemoteData.getJSON(address, properties);

        try {
            if (result.getInt("cod") != 200) {
                return null;
            }
        }catch(Exception e){
            return null;
        }

        return result;
    }
}

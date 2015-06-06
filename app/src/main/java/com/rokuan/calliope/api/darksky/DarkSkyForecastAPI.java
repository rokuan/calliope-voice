package com.rokuan.calliope.api.darksky;

import android.content.Context;
import android.location.Location;

import com.rokuan.calliope.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LEBEAU Christophe on 28/05/15.
 */
public class DarkSkyForecastAPI {
    private static final String FORECAST_API_ADDRESS = "https://api.forecast.io/forecast/%s/";

    public static String getAPIKey(Context context){
        return context.getResources().getString(R.string.darksky_api_key);
    }

    public static String getForecastURL(Context context, Location location, Date date){
        String finalURL = String.format(FORECAST_API_ADDRESS, getAPIKey(context));

        finalURL += location.getLatitude() + "," + location.getLongitude();

        if(date != null){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            //finalURL += "," + date.getTime();
            finalURL += "," + formatter.format(date);
        }

        return finalURL;
    }

    public static int getDrawableIdFromWeatherType(Context context, String name){
        //clear-day,
                //clear-night,
                // rain,
                // snow,
                // sleet,
                // wind,
                // fog,
                // cloudy,
                // partly-cloudy-day, or
                // partly-cloudy-night
        String drawableFormatName = name.replaceAll("-", "_");
        int id = context.getResources().getIdentifier(drawableFormatName, "drawable", context.getPackageName());

        if(id == 0){
            // TODO: trouver une icone par defaut
            id = R.drawable.clear_day;
        }

        return id;
    }
}

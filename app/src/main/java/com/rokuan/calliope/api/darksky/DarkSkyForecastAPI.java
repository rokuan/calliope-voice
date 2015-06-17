package com.rokuan.calliope.api.darksky;

import android.content.Context;
import android.location.Location;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;
import com.rokuan.calliope.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by LEBEAU Christophe on 28/05/15.
 */
public class DarkSkyForecastAPI {
    private static final String FORECAST_API_ADDRESS = "https://api.forecast.io/forecast/%s/";

    private static SyncHttpClient syncClient = new SyncHttpClient();

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

    public static void getSync(Context context, Location location, Date date, ResponseHandlerInterface handler){
        RequestParams params = new RequestParams();
        String url = DarkSkyForecastAPI.getForecastURL(context, location, date);

        params.put("exclude", "minutely,hourly");
        params.put("lang", Locale.getDefault().getLanguage());
        params.put("units", "si");

        syncClient.get(url, params, handler);
    }

    public static int getDrawableIdFromWeatherType(Context context, String name){
        // clear-day,
        // clear-night,
        // rain,
        // snow,
        // sleet,
        // wind,
        // fog,
        // cloudy,
        // partly-cloudy-day,
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

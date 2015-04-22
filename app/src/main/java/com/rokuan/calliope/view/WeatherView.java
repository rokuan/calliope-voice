package com.rokuan.calliope.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rokuan.calliope.R;
import com.rokuan.calliope.api.openweather.WeatherData;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class WeatherView extends LinearLayout {
    private WeatherData data;

    public WeatherView(Context context, WeatherData wData) {
        super(context);
        data = wData;
        initWeatherView();
    }

    private void initWeatherView(){
        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.view_weather, this);

        TextView placeName = (TextView)this.findViewById(R.id.view_weather_place_name);
        ImageView weatherImage = (ImageView)this.findViewById(R.id.view_weather_image);
        TextView temperature = (TextView)this.findViewById(R.id.view_weather_temperature);

        placeName.setText(data.getPlace().toString());
        weatherImage.setImageBitmap(data.getWeatherImage());
        temperature.setText(Math.round(data.getTemperature()) + "°C");
        // TODO: ajouter les autres champs
    }
}

package com.rokuan.calliope.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rokuan.calliope.R;
import com.rokuan.calliope.api.openweather.ForecastData;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class ForecastView extends LinearLayout {

    private ForecastData data;

    public ForecastView(Context context, ForecastData fData) {
        super(context);
        data = fData;
        initForecastView();
    }

    private void initForecastView(){
        //this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.view_forecast, this);

        TextView placeName = (TextView)this.findViewById(R.id.view_forecast_place);
        ListView listView = (ListView)this.findViewById(R.id.view_forecast_list);

        placeName.setText(data.getCity().toString());
        listView.setAdapter(new SingleWeatherDataAdapter(this.getContext(), data.getForecast()));

        Log.i("Data count", String.valueOf(data.getForecast().size()));
    }

    class SingleWeatherDataAdapter extends ArrayAdapter<ForecastData.SingleWeatherData> {
        private LayoutInflater inflater;

        public SingleWeatherDataAdapter(Context context, List<ForecastData.SingleWeatherData> objects) {
            super(context, R.layout.view_forecast_item, objects);
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View v = convertView;

            if(v == null){
                v = inflater.inflate(R.layout.view_forecast_item, parent, false);
            }

            ForecastData.SingleWeatherData item = this.getItem(position);

            TextView date = (TextView)v.findViewById(R.id.view_forecast_item_date);
            ImageView icon = (ImageView)v.findViewById(R.id.view_forecast_item_image);
            TextView temperature = (TextView)v.findViewById(R.id.view_forecast_item_temperature);

            date.setText(new SimpleDateFormat("EEE dd MMMMM").format(item.getDate()));
            icon.setImageBitmap(item.getWeatherImage());
            temperature.setText(item.getTemperature() + "°C");

            return v;
        }
    }
}

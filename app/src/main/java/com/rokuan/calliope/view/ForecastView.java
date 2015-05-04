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
import com.rokuan.calliope.api.openweather.OpenWeatherMapAPI;
import com.squareup.picasso.Picasso;

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
        SingleWeatherDataAdapter adapter = new SingleWeatherDataAdapter(this.getContext(), data.getForecast());

        placeName.setText(data.getCity().toString());
        listView.setAdapter(adapter);

        int totalItemsHeight = 0;
        int itemsCount = adapter.getCount();

        for (int itemPos = 0; itemPos < itemsCount; itemPos++) {
            View item = adapter.getView(itemPos, null, listView);
            item.measure(0, 0);
            totalItemsHeight += item.getMeasuredHeight();
        }

        // Get total height of all item dividers.
        int totalDividersHeight = listView.getDividerHeight() * (itemsCount - 1);

        // Set list height.
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalItemsHeight + totalDividersHeight;
        listView.setLayoutParams(params);
        listView.requestLayout();
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
            //TextView temperature = (TextView)v.findViewById(R.id.view_forecast_item_temperature);
            TextView minTemperature = (TextView)v.findViewById(R.id.view_forecast_item_min_temperature);
            TextView maxTemperature = (TextView)v.findViewById(R.id.view_forecast_item_max_temperature);

            date.setText(new SimpleDateFormat("EEE dd MMMMM").format(item.getDate()));
            //icon.setImageBitmap(item.getWeatherImage());
            Picasso.with(this.getContext()).load(OpenWeatherMapAPI.getBitmapURL(item.getWeatherIconName())).into(icon);
            //temperature.setText(Math.round(item.getTemperature()) + "°C");
            // TODO: verifier l'unite de la temperature

            minTemperature.setText(Math.round(item.getMinTemperature()) + "°C");
            maxTemperature.setText(Math.round(item.getMaxTemperature()) + "°C");

            return v;
        }
    }
}

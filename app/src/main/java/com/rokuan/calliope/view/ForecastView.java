package com.rokuan.calliope.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rokuan.calliope.R;
import com.rokuan.calliope.api.darksky.DarkSkyForecastAPI;
import com.rokuan.calliope.api.darksky.ForecastData;
import com.rokuan.calliope.api.openweather.OpenWeatherMapAPI;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class ForecastView extends LinearLayout {
    private ForecastData data;

    @InjectView(R.id.view_forecast_place) protected TextView placeName;
    @InjectView(R.id.view_forecast_list) protected ListView forecastListView;

    public ForecastView(Context context, ForecastData fData) {
        super(context);
        data = fData;
        initForecastView();
    }

    private void initForecastView(){
        //this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_forecast, this);
        ButterKnife.inject(this);

        SingleWeatherDataAdapter adapter = new SingleWeatherDataAdapter(this.getContext(), data.getBlocks());

        placeName.setText(data.getCity().toString());
        forecastListView.setAdapter(adapter);

        int totalItemsHeight = 0;
        int itemsCount = adapter.getCount();

        for (int itemPos = 0; itemPos < itemsCount; itemPos++) {
            View item = adapter.getView(itemPos, null, forecastListView);
            item.measure(0, 0);
            totalItemsHeight += item.getMeasuredHeight();
        }

        // Get total height of all item dividers.
        int totalDividersHeight = forecastListView.getDividerHeight() * (itemsCount - 1);

        // Set list height.
        ViewGroup.LayoutParams params = forecastListView.getLayoutParams();
        params.height = totalItemsHeight + totalDividersHeight;
        forecastListView.setLayoutParams(params);
        forecastListView.requestLayout();
    }

    class SingleWeatherDataAdapter extends ArrayAdapter<ForecastData.ForecastBlock> {
        private LayoutInflater inflater;

        public SingleWeatherDataAdapter(Context context, List<ForecastData.ForecastBlock> objects) {
            super(context, R.layout.view_forecast_item, objects);
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View v = convertView;

            if(v == null){
                v = inflater.inflate(R.layout.view_forecast_item, parent, false);
            }

            ForecastData.ForecastBlock item = this.getItem(position);

            TextView date = (TextView)v.findViewById(R.id.view_forecast_item_date);
            ImageView icon = (ImageView)v.findViewById(R.id.view_forecast_item_image);
            //TextView temperature = (TextView)v.findViewById(R.id.view_forecast_item_temperature);
            TextView minTemperature = (TextView)v.findViewById(R.id.view_forecast_item_min_temperature);
            TextView maxTemperature = (TextView)v.findViewById(R.id.view_forecast_item_max_temperature);

            date.setText(new SimpleDateFormat("EEE dd MMMMM").format(item.getDate()));
            Picasso.with(this.getContext()).load(DarkSkyForecastAPI.getDrawableIdFromWeatherType(this.getContext(), item.getIcon())).into(icon);
            // TODO: verifier l'unite de la temperature
            minTemperature.setText(Math.round(item.getMinTemperature()) + "°C");
            maxTemperature.setText(Math.round(item.getMaxTemperature()) + "°C");

            return v;
        }
    }
}

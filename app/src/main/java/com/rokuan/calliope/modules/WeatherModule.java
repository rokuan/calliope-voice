package com.rokuan.calliope.modules;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.rokuan.calliope.HomeActivity;
import com.rokuan.calliope.api.ResultCallback;
import com.rokuan.calliope.api.darksky.DarkSkyForecastAPI;
import com.rokuan.calliope.api.darksky.ForecastData;
import com.rokuan.calliope.source.ForecastSource;
import com.rokuan.calliope.utils.Connectivity;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.data.place.PlaceObject;
import com.rokuan.calliopecore.sentence.structure.data.place.StateObject;
import com.rokuan.calliopecore.sentence.structure.data.time.SingleTimeObject;
import com.rokuan.calliopecore.sentence.structure.data.time.TimeObject;
import com.rokuan.calliopecore.sentence.structure.nominal.ComplementObject;
import com.rokuan.calliopecore.sentence.structure.nominal.NominalGroup;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class WeatherModule extends CalliopeModule implements ResultCallback<ForecastData> {
    private static final String WEATHER_CONTENT_REGEX = "(((température)(s?))|temps)";
    private static final String FORECAST_CONTENT_REGEX = "(météo|((prévision)(s?)))";
    private static final String CONTENT_REGEX = "(" + WEATHER_CONTENT_REGEX + "|" + FORECAST_CONTENT_REGEX + ")";

    private static final int WEATHER = 0;
    private static final int FORECAST = 1;

    class ForecastAsyncTask extends AsyncTask<Object, Void, ForecastData> {
        private String cityName;
        private JSONObject data;
        private boolean success = true;

        @Override
        protected void onPreExecute(){
            WeatherModule.this.getActivity().startProcess();
        }

        @Override
        protected ForecastData doInBackground(Object... objects) {
            Location currentLocation = (Location)objects[0];
            Date date = (Date)objects[1];
            Geocoder geocoder = new Geocoder(WeatherModule.this.getActivity());

            try {
                List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                cityName = addresses.get(0).getLocality();
                Log.i("CityName", cityName);
            } catch (IOException e) {
                e.printStackTrace();
                success = false;
            }

            if(success){
                DarkSkyForecastAPI.getSync(WeatherModule.this.getActivity(), currentLocation, date, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject result) {
                        data = result;
                        try {
                            data.put("_additional_city_attribute", cityName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        success = false;
                    }
                });
            }

            try {
                return ForecastData.buildFromJSON(data);
            } catch (JSONException e) {
                e.printStackTrace();
                success = false;
                return null;
            }
        }

        @Override
        protected void onPostExecute(ForecastData result){
            WeatherModule.this.onResult(success, result);
            WeatherModule.this.getActivity().endProcess();
        }
    }

    public WeatherModule(HomeActivity a) {
        super(a);
    }

    @Override
    public boolean canHandle(InterpretationObject object) {
        if(object.what != null && object.what.getType() == NominalGroup.GroupType.COMPLEMENT){
            ComplementObject compl = (ComplementObject)object.what;

            if(compl.object.matches(CONTENT_REGEX)){
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean submit(InterpretationObject object) {
        // TODO:
        if(object.what != null && object.what.getType() == NominalGroup.GroupType.COMPLEMENT){
            ComplementObject compl = (ComplementObject)object.what;

            if(compl.object.matches(WEATHER_CONTENT_REGEX)){
                StateObject stateLocation = null;
                TimeObject period;

                if(object.where != null && object.where.getType() == NominalGroup.GroupType.STATE){
                    stateLocation = (StateObject)object.where;
                }

                if(object.when != null){
                    period = object.when;
                } else if(compl.when != null){
                    period = compl.when;
                } else {
                    SingleTimeObject todayObject = new SingleTimeObject();
                    todayObject.dateDefinition = TimeObject.DateDefinition.DATE_AND_TIME;
                    todayObject.date = new Date();
                    period = todayObject;
                }

                fetchWeatherData(WEATHER, stateLocation, period);
                return true;
            } else if(compl.object.matches(FORECAST_CONTENT_REGEX)){
                StateObject stateLocation = null;
                TimeObject period = null;

                if(object.where != null && object.where.getType() == NominalGroup.GroupType.STATE){
                    stateLocation = (StateObject)object.where;
                }

                if(object.when != null){
                    period = object.when;
                } else if(compl.when != null){
                    period = compl.when;
                }

                fetchWeatherData(FORECAST, stateLocation, period);
                return true;
            }
        }

        return false;
    }

    private void fetchWeatherData(int dataType, StateObject state, TimeObject period){
        if(!Connectivity.isNetworkAvailable(this.getActivity())){
            Toast.makeText(this.getActivity(), "Aucune connexion internet", Toast.LENGTH_SHORT).show();
            return;
        }

        Location currentLocation = null;
        Date date = null;

        if(state == null){
            currentLocation = this.getActivity().getCurrentLocation();
        } else {
            // TODO: recuperer ville (+ pays)
            Log.i("Location specified", state.city.getName());

            if(state.city != null) {
                currentLocation = new Location("");
                currentLocation.setLatitude(state.city.getLatitude());
                currentLocation.setLongitude(state.city.getLongitude());
            }
        }

        if(period != null){
            switch(period.getType()){
                case SINGLE:
                    date = ((SingleTimeObject)period).date;
                    break;
            }
        }

        new ForecastAsyncTask().execute(currentLocation, date);
    }

    @Override
    public void onResult(boolean success, ForecastData result) {
        if(success){
            this.getActivity().addSource(new ForecastSource(result));
        } else {
            // TODO:
        }
    }
}

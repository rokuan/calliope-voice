package com.rokuan.calliope.modules;

import android.location.Location;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
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

import java.util.Date;
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
                TimeObject period = null;

                if(object.where != null && object.where.getType() == PlaceObject.PlaceType.STATE){
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

                if(object.where != null && object.where.getType() == PlaceObject.PlaceType.STATE){
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
        }

        if(period != null){
            switch(period.getType()){
                case SINGLE:
                    date = ((SingleTimeObject)period).date;
                    break;
            }
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("exclude", "minutely,hourly");
        params.put("lang", Locale.getDefault().getLanguage());
        params.put("units", "si");

        client.get(DarkSkyForecastAPI.getForecastURL(this.getActivity(), currentLocation, date), params, new JsonHttpResponseHandler(){
            @Override
            public void onStart(){
                WeatherModule.this.getActivity().startProcess();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject result) {
                try {
                    WeatherModule.this.onResult(true, ForecastData.buildFromJSON(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                    WeatherModule.this.onResult(false, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                WeatherModule.this.onResult(false, null);
            }

            @Override
            public void onFinish(){
                WeatherModule.this.getActivity().endProcess();
            }
        });
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

package com.rokuan.calliope.modules;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rokuan.calliope.HomeActivity;
import com.rokuan.calliope.api.ResultCallback;
import com.rokuan.calliope.api.kimonotv.TVProgram;
import com.rokuan.calliope.api.openweather.ForecastData;
import com.rokuan.calliope.api.openweather.ForecastDataResultCallback;
import com.rokuan.calliope.api.openweather.OpenWeatherMapAPI;
import com.rokuan.calliope.api.openweather.WeatherData;
import com.rokuan.calliope.api.openweather.WeatherDataResultCallback;
import com.rokuan.calliope.source.ForecastSource;
import com.rokuan.calliope.source.WeatherSource;
import com.rokuan.calliope.utils.Connectivity;
import com.rokuan.calliope.utils.RemoteData;
import com.rokuan.calliope.view.ForecastView;
import com.rokuan.calliope.view.WeatherView;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.data.place.PlaceObject;
import com.rokuan.calliopecore.sentence.structure.data.place.StateObject;
import com.rokuan.calliopecore.sentence.structure.nominal.ComplementObject;
import com.rokuan.calliopecore.sentence.structure.nominal.NominalGroup;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class WeatherModule extends CalliopeModule implements WeatherDataResultCallback, ForecastDataResultCallback {
    private static final String WEATHER_CONTENT_REGEX = "(((température)(s?))|temps)";
    private static final String FORECAST_CONTENT_REGEX = "(météo|((prévision)(s?)))";
    private static final String CONTENT_REGEX = "(" + WEATHER_CONTENT_REGEX + "|" + FORECAST_CONTENT_REGEX + ")";

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

                if(object.where != null && object.where.getType() == PlaceObject.PlaceType.STATE){
                    stateLocation = (StateObject)object.where;
                }

                fetchWeatherData(stateLocation);
                return true;
            } else if(compl.object.matches(FORECAST_CONTENT_REGEX)){
                StateObject stateLocation = null;

                if(object.where != null && object.where.getType() == PlaceObject.PlaceType.STATE){
                    stateLocation = (StateObject)object.where;
                }

                fetchForecastData(stateLocation);
                return true;
            }
        }

        return false;
    }

    private void fetchWeatherData(StateObject state){
        if(!Connectivity.isNetworkAvailable(this.getActivity())){
            Toast.makeText(this.getActivity(), "Aucune connexion internet", Toast.LENGTH_SHORT).show();
            return;
        }

        if(state == null){
            //new WeatherAsyncTask(this.getActivity()).execute(this.getActivity().getCurrentLocation());
            AsyncHttpClient client = new AsyncHttpClient();

            client.get(OpenWeatherMapAPI.getWeatherForLocationURL(this.getActivity(), this.getActivity().getCurrentLocation()),
                    OpenWeatherMapAPI.getAdditionalParameters(this.getActivity()),
                    new JsonHttpResponseHandler(){
                        @Override
                        public void onStart(){
                            WeatherModule.this.getActivity().startProcess();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                WeatherModule.this.onWeatherDataResult(true, WeatherData.buildFromJSON(WeatherModule.this.getActivity(), response));
                            } catch (JSONException e) {
                                WeatherModule.this.onWeatherDataResult(false, null);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            WeatherModule.this.onWeatherDataResult(false, null);
                        }

                        @Override
                        public void onFinish(){
                            WeatherModule.this.getActivity().endProcess();
                        }
                    });
        } else {
            // TODO: recuperer ville (+ pays)
        }
    }

    @Override
    public void onWeatherDataResult(boolean success, WeatherData result) {
        if(success){
            Log.i("WeatherModule", "onWeatherDataResult");
            //this.getActivity().insertView(new WeatherView(this.getActivity(), result));
            this.getActivity().addSource(new WeatherSource(result));
        } else {
            // TODO:
        }
    }

    private void fetchForecastData(StateObject state){
        if(!Connectivity.isNetworkAvailable(this.getActivity())){
            Toast.makeText(this.getActivity(), "Aucune connexion internet", Toast.LENGTH_SHORT).show();
            return;
        }

        if(state == null){
            AsyncHttpClient client = new AsyncHttpClient();

            client.get(OpenWeatherMapAPI.getWeekForecastForLocationURL(this.getActivity(), this.getActivity().getCurrentLocation()),
                    OpenWeatherMapAPI.getAdditionalParameters(this.getActivity()),
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            WeatherModule.this.getActivity().startProcess();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                WeatherModule.this.onForecastDataResult(true, ForecastData.buildFromJSON(response));
                            } catch (JSONException e) {
                                WeatherModule.this.onForecastDataResult(false, null);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            WeatherModule.this.onForecastDataResult(false, null);
                        }

                        @Override
                        public void onFinish() {
                            WeatherModule.this.getActivity().endProcess();
                        }
                    });
        } else {
            // TODO: recuperer ville (+ pays)
        }
    }

    @Override
    public void onForecastDataResult(boolean success, ForecastData result) {
        if(success){
            //this.getActivity().insertView(new ForecastView(this.getActivity(), result));
            this.getActivity().addSource(new ForecastSource(result));
        } else {
            // TODO:
        }
    }
}

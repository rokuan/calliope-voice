package com.rokuan.calliope.modules;

import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;

import com.rokuan.calliope.HomeActivity;
import com.rokuan.calliope.api.openweather.ForecastData;
import com.rokuan.calliope.api.openweather.OpenWeatherMapAPI;
import com.rokuan.calliope.api.openweather.WeatherData;
import com.rokuan.calliope.utils.Connectivity;
import com.rokuan.calliope.view.ForecastView;
import com.rokuan.calliope.view.WeatherView;
import com.rokuan.calliopecore.sentence.structure.ComplementObject;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.NominalGroup;
import com.rokuan.calliopecore.sentence.structure.data.place.PlaceObject;
import com.rokuan.calliopecore.sentence.structure.data.place.StateObject;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class WeatherModule extends CalliopeModule {
    private static final String WEATHER_CONTENT_REGEX = "(((température)(s?))|temps)";
    private static final String FORECAST_CONTENT_REGEX = "(météo|((prévision)(s?)))";
    private static final String CONTENT_REGEX = "(" + WEATHER_CONTENT_REGEX + "|" + FORECAST_CONTENT_REGEX + ")";

    class WeatherAsyncTask extends AsyncTask<Object, Integer, WeatherData> {
        private HomeActivity act;

        public WeatherAsyncTask(HomeActivity a){
            act = a;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            act.startProcess();
        }

        @Override
        protected WeatherData doInBackground(Object... params) {
            if(params[0] == null){
                return null;
            }

            if(params[0] instanceof Location){
                return OpenWeatherMapAPI.getWeatherForLocation(act, (Location)params[0]);
            } else {
                return OpenWeatherMapAPI.getWeatherForName(act, params[0].toString());
            }
        }

        @Override
        protected void onPostExecute(WeatherData data){
            super.onPostExecute(data);

            if(data == null){
                // TODO: afficher un message d'erreur
            } else {
                act.insertView(new WeatherView(act, data));
            }

            act.endProcess();
        }
    }

    class ForecastAsyncTask extends AsyncTask<Object, Integer, ForecastData> {
        private HomeActivity act;

        public ForecastAsyncTask(HomeActivity a){
            act = a;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            act.startProcess();
        }

        @Override
        protected ForecastData doInBackground(Object... params) {
            if(params[0] instanceof Location){
                return OpenWeatherMapAPI.getWeekForecastForLocation(act, (Location)params[0]);
            } else {
                return OpenWeatherMapAPI.getWeekForecastForName(act, params[0].toString());
            }
        }

        @Override
        protected void onPostExecute(ForecastData data){
            super.onPostExecute(data);

            if(data == null){
                // TODO:
            } else {
                act.insertView(new ForecastView(act, data));
            }

            act.endProcess();
        }
    }

    public WeatherModule(HomeActivity a) {
        super(a);
    }

    @Override
    public boolean canHandle(InterpretationObject object) {
        if(object.what != null && object.what.getType() == NominalGroup.GroupType.NOMINAL_GROUP){
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
        if(object.what != null && object.what.getType() == NominalGroup.GroupType.NOMINAL_GROUP){
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
            new WeatherAsyncTask(this.getActivity()).execute(this.getActivity().getCurrentLocation());
        } else {
            // TODO: recuperer ville (+ pays)
        }
    }

    private void fetchForecastData(StateObject state){
        if(!Connectivity.isNetworkAvailable(this.getActivity())){
            Toast.makeText(this.getActivity(), "Aucune connexion internet", Toast.LENGTH_SHORT).show();
            return;
        }

        if(state == null){
            new ForecastAsyncTask(this.getActivity()).execute(this.getActivity().getCurrentLocation());
        } else {
            // TODO: recuperer ville (+ pays)
        }
    }
}

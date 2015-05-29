package com.rokuan.calliope.api.darksky;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by LEBEAU Christophe on 28/05/15.
 */
public class ForecastData {
    private String city;
    private List<ForecastBlock> blocks = new ArrayList<>();

    public static class ForecastBlock {
        private Date date;
        //:1434092400,
        private String summary;
        //"Nuages épars durant toute la journée.",
        private String icon;
        //"partly-cloudy-day",
        private Date sunrise;
        //1434113309,
        private Date sunset;
        //1434166400,
        //private double moonPhase;
        //0.87,
        //private String precipitationType;
        //"rain",
        private double minTemperature;
        //12.09
        private double maxTemperature;
        //16.27
        //private double minApparentTemperature;
        //12.09
        //private double maxApparentTemperature;
        //16.27
        private double humidity;
        //0.75
        private double windSpeed;
        //4.72
        private double pressure;
        //1015.45

        public static ForecastBlock buildFromJSON(JSONObject json) throws JSONException {
            ForecastBlock block = new ForecastBlock();

            block.date = new Date(json.getLong("time") * 1000);
            block.summary = json.getString("summary");
            // TODO:
            block.icon = null;
            block.sunrise = new Date(json.getLong("sunriseTime") * 1000);
            block.sunset = new Date(json.getLong("sunsetTime") * 1000);
            //block.precipitationType = json.getString("precipType");
            block.minTemperature = json.getDouble("temperatureMin");
            block.maxTemperature = json.getDouble("temperatureMax");
            block.humidity = json.getDouble("humidity");
            block.windSpeed = json.getDouble("windSpeed");
            block.pressure = json.getDouble("pressure");

            return block;
        }

        public Date getDate() {
            return date;
        }

        public String getSummary() {
            return summary;
        }

        public String getIcon() {
            return icon;
        }

        public Date getSunrise() {
            return sunrise;
        }

        public Date getSunset() {
            return sunset;
        }

        /*public String getPrecipitationType() {
            return precipitationType;
        }*/

        public double getMinTemperature() {
            return minTemperature;
        }

        public double getMaxTemperature() {
            return maxTemperature;
        }

        public double getHumidity() {
            return humidity;
        }

        public double getWindSpeed() {
            return windSpeed;
        }

        public double getPressure() {
            return pressure;
        }
    }

    private ForecastData(){}

    public static ForecastData buildFromJSON(JSONObject json) throws JSONException {
        ForecastData data = new ForecastData();

        String timezone = json.getString("timezone");
        data.city = timezone.substring(timezone.indexOf('/') + 1);

        JSONArray dailyData = json.getJSONObject("daily").getJSONArray("data");

        for(int i=0; i<dailyData.length(); i++){
            data.blocks.add(ForecastBlock.buildFromJSON(dailyData.getJSONObject(i)));
        }

        return data;
    }

    public String getCity() {
        return city;
    }

    public List<ForecastBlock> getBlocks(){
        return blocks;
    }
}

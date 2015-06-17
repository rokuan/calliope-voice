package com.rokuan.calliope.api.google;

import android.location.Location;

/**
 * Created by LEBEAU Christophe on 17/06/15.
 */
public class GeocodingAPI {
    public static final String GEOCODE_URL = "http://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=true&lang=%s";

    public static String getGeocodeUrl(Location location, String language){
        return String.format(GEOCODE_URL, location.getLatitude(), location.getLongitude(), language);
    }
}

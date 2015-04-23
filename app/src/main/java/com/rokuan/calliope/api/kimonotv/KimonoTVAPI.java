package com.rokuan.calliope.api.kimonotv;

import com.rokuan.calliope.utils.RemoteData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LEBEAU Christophe on 23/04/2015.
 */
public class KimonoTVAPI {
    private static final String API_ADDRESS = "https://www.kimonolabs.com/api/afxopnoq?&apikey=6e1c9495fe8d4f9921da587e4d007465&kimmodify=1";

    public static List<TVProgram> getTVListings(){
        JSONObject listings = RemoteData.getJSON(API_ADDRESS, null);

        try {
            JSONArray programsArray = listings.getJSONArray("programms");
            List<TVProgram> programs = new ArrayList<>(programsArray.length());

            for(int i=0; i<programsArray.length(); i++){
                programs.add(TVProgram.buildFromJSON(programsArray.getJSONObject(i)));
            }

            return programs;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}

package com.rokuan.calliope.modules;

import android.os.AsyncTask;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rokuan.calliope.HomeActivity;
import com.rokuan.calliope.api.ResultCallback;
import com.rokuan.calliope.api.kimonotv.KimonoTVAPI;
import com.rokuan.calliope.api.kimonotv.TVProgram;
import com.rokuan.calliope.source.TVListingsSource;
import com.rokuan.calliope.utils.Connectivity;
import com.rokuan.calliopecore.sentence.Action;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.QuestionObject;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class TVModule extends CalliopeModule implements ResultCallback<List<TVProgram>> {
    private static final String API_ADDRESS = "https://www.kimonolabs.com/api/afxopnoq?&apikey=6e1c9495fe8d4f9921da587e4d007465&kimmodify=1";

    private static final String WHERE_CONTENT_REGEX = "(télé((vision)?))";
    private static final String CONTENT_REGEX = "(émission|programme|chaîne)";

    public TVModule(HomeActivity a) {
        super(a);
    }

    @Override
    public boolean canHandle(InterpretationObject object) {
        /*if(object.what != null){
            if(object.what.object.matches(CONTENT_REGEX)){
                switch((Action.VerbAction)object.action){
                    case SHOW:
                    case DISPLAY_SHOW:
                    case GIVE__PASS:

                        return true;
                }
            }
        }

        return false;*/
        // TODO:
        return object.getDescription().matches(".*:" + CONTENT_REGEX);
    }

    @Override
    public boolean submit(InterpretationObject object) {
        if(object.getType() == InterpretationObject.RequestType.QUESTION){
            QuestionObject question = (QuestionObject)object;

            if(question.qType == QuestionObject.QuestionType.WHAT){
                //question.
                return true;
            }
        } else if(object.getType() == InterpretationObject.RequestType.ORDER){
            switch((Action.VerbAction)object.action){
                default:
                    int channelNumber = -1;
                    String channelName = null;

                    findListings(channelName, channelNumber);
                    return true;
            }
        }

        return false;
    }

    private void findListings(String channelName, int channelNumber){
        if(!Connectivity.isNetworkAvailable(this.getActivity())){
            // TODO:
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(API_ADDRESS, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                TVModule.this.getActivity().startProcess();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject listings) {
                try {
                    JSONArray programsArray = listings.getJSONArray("programms");
                    List<TVProgram> programs = new ArrayList<>(programsArray.length());

                    for (int i = 0; i < programsArray.length(); i++) {
                        programs.add(TVProgram.buildFromJSON(programsArray.getJSONObject(i)));
                    }

                    TVModule.this.onResult(true, programs);
                } catch (JSONException e) {
                    //e.printStackTrace();
                    TVModule.this.onResult(false, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                TVModule.this.onResult(false, null);
            }

            @Override
            public void onFinish() {
                TVModule.this.getActivity().endProcess();
            }
        });

        //new TVListingAsyncTask(this.getActivity()).execute();
    }

    @Override
    public void onResult(boolean success, List<TVProgram> result) {
        if(success){
            this.getActivity().addSource(new TVListingsSource(result));
        } else {

        }
    }
}

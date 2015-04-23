package com.rokuan.calliope.modules;

import android.content.Context;
import android.os.AsyncTask;

import com.rokuan.calliope.HomeActivity;
import com.rokuan.calliope.api.kimonotv.KimonoTVAPI;
import com.rokuan.calliope.api.kimonotv.TVProgram;
import com.rokuan.calliope.utils.Connectivity;
import com.rokuan.calliope.view.TVListingView;
import com.rokuan.calliopecore.sentence.Action;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.QuestionObject;

import java.util.List;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class TVModule extends CalliopeModule {
    private static final String WHERE_CONTENT_REGEX = "(télé((vision)?))";
    private static final String CONTENT_REGEX = "(émission|programme|chaîne)";

    class TVListingAsyncTask extends AsyncTask<String, Void, List<TVProgram>> {
        private HomeActivity act;

        public TVListingAsyncTask(HomeActivity a){
            act = a;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            act.startProcess();
        }

        @Override
        protected List<TVProgram> doInBackground(String... params) {
            return KimonoTVAPI.getTVListings();
        }

        @Override
        protected void onPostExecute(List<TVProgram> results){
            super.onPostExecute(results);

            if(results == null){
                // TODO:
            } else {
                act.insertView(new TVListingView(act, results));
            }

            act.endProcess();
        }
    }

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

        new TVListingAsyncTask(this.getActivity()).execute();
    }
}

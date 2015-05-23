package com.rokuan.calliope.modules;

import android.os.AsyncTask;

import com.rokuan.calliope.HomeActivity;
import com.rokuan.calliope.api.ResultCallback;
import com.rokuan.calliope.api.microsoft.TranslationData;
import com.rokuan.calliope.api.microsoft.MicrosoftTranslatorAPI;
import com.rokuan.calliope.source.SourceObject;
import com.rokuan.calliope.source.TextSource;
import com.rokuan.calliope.source.TranslationSource;
import com.rokuan.calliopecore.sentence.Action;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.nominal.LanguageObject;
import com.rokuan.calliopecore.sentence.structure.nominal.NominalGroup;

import java.util.Locale;

/**
 * Created by LEBEAU Christophe on 18/05/15.
 */
public class LanguageModule extends CalliopeModule implements ResultCallback<TranslationData> {
    class MicrosoftTranslatorAsyncTask extends AsyncTask<String, Void, TranslationData> {
        private HomeActivity activity;
        private boolean success = false;

        public MicrosoftTranslatorAsyncTask(HomeActivity act){
            activity = act;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            activity.startProcess();
        }

        /*private synchronized void setSuccess(boolean s){
            success = s;
        }*/

        @Override
        protected TranslationData doInBackground(String... strings) {
            TranslationData data = MicrosoftTranslatorAPI.translateText(activity, strings[0], strings[1]);

            if(data != null){
                success = true;
            }

            return data;
        }

        @Override
        protected void onPostExecute(TranslationData result){
            if(success){
                activity.addSource(new TranslationSource(result));
            } else {
                // TODO: ajouter un message d'erreur
            }

            activity.endProcess();
        }
    }

    public LanguageModule(HomeActivity a) {
        super(a);
    }

    @Override
    public boolean canHandle(InterpretationObject object) {
        if(object.getType() == InterpretationObject.RequestType.ORDER){
            switch((Action.VerbAction)object.action){
                case TRANSLATE:
                    // TODO: verifier
                    return true;
            }
        }

        return false;
    }

    @Override
    public boolean submit(InterpretationObject object) {
        if(object.getType() == InterpretationObject.RequestType.ORDER){
            switch((Action.VerbAction)object.action){
                case TRANSLATE:
                case CONVERT:
                    SourceObject.ObjectType defaultType = SourceObject.ObjectType.TEXT;
                    String langCode = Locale.getDefault().getLanguage();

                    if(object.how != null && object.how.getType() == NominalGroup.GroupType.LANGUAGE){
                        langCode = ((LanguageObject)object.how).language.getCode();
                    }

                    TextSource txtSource = (TextSource)this.getActivity().getLastSourceOfType(defaultType);

                    if(txtSource != null) {
                        //System.out.println("Traduction du texte \"" + txtSource.getText() +"\" en \"" + langCode + "\"");
                        translateText(txtSource.getText(), langCode);
                        return true;
                    } else {
                        // TODO: afficher un message d'erreur
                    }
            }
        }

        return false;
    }

    private void translateText(final String text, final String toLang){
        // TODO: verifier la connexion a internet
        new MicrosoftTranslatorAsyncTask(this.getActivity()).execute(text, toLang);

        /*AsyncHttpClient client = new AsyncHttpClient();
        String apiURL = GoogleTranslateAPI.getTranslationURL(this.getActivity(), text, null, toLang);

        System.out.println(apiURL);

        client.get(apiURL, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                LanguageModule.this.getActivity().startProcess();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject translation) {
                System.out.println("SUCCESS => hello");

                try {
                    JSONArray translatedTexts = translation.getJSONObject("data").getJSONArray("translations");
                    JSONObject firstTranslation = translatedTexts.getJSONObject(0);
                    String original = text;
                    String translated = firstTranslation.getString("translatedText");
                    String fromLang = firstTranslation.getString("detectedSourceLanguage");

                    System.out.println("Translated to " + translated + "(" + toLang + ")");

                    LanguageModule.this.onResult(true, new TranslationData(fromLang, toLang, original, translated));
                } catch (JSONException e) {
                    LanguageModule.this.onResult(false, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                System.err.println(errorResponse);
                LanguageModule.this.onResult(false, null);
            }

            @Override
            public void onFinish() {
                LanguageModule.this.getActivity().endProcess();
            }
        });*/

    }

    @Override
    public void onResult(boolean success, TranslationData result) {
        if(success){
            this.getActivity().addSource(new TranslationSource(result));
        } else {
            // TODO:
        }
    }
}

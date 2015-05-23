package com.rokuan.calliope.api.microsoft;

import android.content.Context;
import android.util.Xml;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rokuan.calliope.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by Lebeau Lucie on 22/05/15.
 */
public class MicrosoftTranslatorAPI {
    /*class AdmAuthenticationAsyncHttpClient extends AsyncHttpClient {
        public void post(String clientId, String clientSecret, @NonNull JsonHttpResponseHandler handler){
            this.pos()
        }
    }*/

    public static void translateText(Context context, final String text, final String destLang, JsonHttpResponseHandler handler){
        AsyncHttpClient tokenClient = new AsyncHttpClient();
        RequestParams postParams = new RequestParams();

        postParams.put("client_id", context.getResources().getString(R.string.microsoft_client_id));
        postParams.put("client_secret", context.getResources().getString(R.string.microsoft_client_key));
        postParams.put("grant_type", "client_credentials");
        postParams.put("scope", "http://api.microsofttranslator.com");

        tokenClient.post("https://datamarket.accesscontrol.windows.net/v2/OAuth2-13", postParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject tokenInfo) {
                try {
                    String accessToken = tokenInfo.getString("access_token");
                    AsyncHttpClient translateClient = new AsyncHttpClient();

                    translateClient.addHeader("Authorization", "Bearer " + accessToken);
                    translateClient.get("http://api.microsofttranslator.com/v2/Http.svc/Detect?text=" + text, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            System.err.println(responseString);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            XmlPullParser parser = Xml.newPullParser();
                            String detectedLanguage = null;

                            try {
                                parser.setInput(new StringReader(responseString));

                                int eventType = parser.getEventType();

                                while (eventType != XmlPullParser.END_TAG) {
                                    switch (eventType) {
                                        case XmlPullParser.START_TAG:
                                            break;

                                        case XmlPullParser.TEXT:
                                            detectedLanguage = parser.getText();
                                            break;

                                        case XmlPullParser.END_TAG:
                                            System.out.println("Detected language: " + detectedLanguage);
                                            break;

                                        default:
                                            break;
                                    }

                                    try {
                                        eventType = parser.next();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if(detectedLanguage != null){
                                    // TODO:
                                }

                                //System.out.println("Tag name: " + parser.getName());
                            } catch (XmlPullParserException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    //System.err.println(e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                System.err.println(errorResponse);
            }
        });
    }
}

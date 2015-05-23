package com.rokuan.calliope.api.microsoft;

import android.content.Context;
import android.util.Xml;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rokuan.calliope.R;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by LEBEAU Christophe on 22/05/15.
 */
public class MicrosoftTranslatorAPI {
    static class AdmAuthenticationClient {
        private String accessToken;
        private Context context;

        public AdmAuthenticationClient(Context c){
            context = c;
        }

        public String getAccessToken(){
            SyncHttpClient tokenClient = new SyncHttpClient();
            RequestParams postParams = new RequestParams();

            postParams.put("client_id", context.getResources().getString(R.string.microsoft_client_id));
            postParams.put("client_secret", context.getResources().getString(R.string.microsoft_client_key));
            postParams.put("grant_type", "client_credentials");
            postParams.put("scope", "http://api.microsofttranslator.com");

            tokenClient.post("https://datamarket.accesscontrol.windows.net/v2/OAuth2-13", postParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject tokenInfo) {
                    try {
                        accessToken = tokenInfo.getString("access_token");
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

            return accessToken;
        }
    }

    static class LanguageDetectionClient {
        private String accessToken;
        private String detectedLanguage;

        public LanguageDetectionClient(String token){
            accessToken = token;
        }

        public String detectLanguage(String text){
            SyncHttpClient languageClient = new SyncHttpClient();

            languageClient.addHeader("Authorization", "Bearer " + accessToken);
            languageClient.get("http://api.microsofttranslator.com/v2/Http.svc/Detect?text=" + text, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    System.err.println(responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    XmlPullParser parser = Xml.newPullParser();

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
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    }
                }
            });

            return detectedLanguage;
        }
    }

    static class TranslationClient {
        private TranslationData data;
        private String accessToken;

        public TranslationClient(String token){
            accessToken = token;
        }

        public TranslationData translate(final String text, final String fromLang, final String toLang){
            SyncHttpClient translationClient = new SyncHttpClient();
            /*RequestParams params = new RequestParams();

            params.put("text", text);
            params.put("from", fromLang);
            params.put("to", toLang);*/

            translationClient.addHeader("Authorization", "Bearer " + accessToken);
            translationClient.get("http://api.microsofttranslator.com/v2/Http.svc/Translate?text=" + text + "&from=" + fromLang + "&to=" + toLang, new TextHttpResponseHandler(){
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    System.err.println(responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    XmlPullParser parser = Xml.newPullParser();

                    try {
                        parser.setInput(new StringReader(responseString));

                        int eventType = parser.getEventType();

                        while (eventType != XmlPullParser.END_TAG) {
                            switch (eventType) {
                                case XmlPullParser.START_TAG:
                                    break;

                                case XmlPullParser.TEXT:
                                    data = new TranslationData(fromLang, toLang, text, parser.getText());
                                    break;

                                case XmlPullParser.END_TAG:
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
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    }
                }
            });

            return data;
        }
    }

    private static String getAccessToken(Context context){
        return new AdmAuthenticationClient(context).getAccessToken();
    }

    private static String detectLanguage(String accessToken, String originalText){
        return new LanguageDetectionClient(accessToken).detectLanguage(originalText);
    }

    private static TranslationData translateText(String accessToken, String text, String sourceLang, String destLang){
        return new TranslationClient(accessToken).translate(text, sourceLang, destLang);
    }

    public static TranslationData translateText(Context context, String text, String destLang){
        String accessToken = getAccessToken(context);

        if(accessToken != null){
            String sourceLang = detectLanguage(accessToken, text);

            if(sourceLang != null){
                return translateText(accessToken, text, sourceLang, destLang);
            }
        }

        return null;

        /*AsyncHttpClient tokenClient = new AsyncHttpClient();
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
        });*/
    }
}

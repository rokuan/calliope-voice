package com.rokuan.calliope.source;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.rokuan.calliope.api.OcrAPI;
import com.rokuan.calliope.extract.ExtractionListener;
import com.rokuan.calliope.extract.HypertextLinkExtractionListener;
import com.rokuan.calliope.extract.HypertextLinkExtractor;
import com.rokuan.calliope.extract.TextExtractionListener;
import com.rokuan.calliope.extract.TextExtractor;
import com.rokuan.calliope.utils.PathResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by LEBEAU Christophe on 01/04/2015.
 */
public class ImageFileSource extends MediaFileSource implements HypertextLinkExtractor, TextExtractor {
    public class OcrTextTask extends AsyncTask<File, Void, String> {
        private ExtractionListener<String> listener;

        public OcrTextTask(ExtractionListener<String> extListener){
            listener = extListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listener.onExtractionStarted("");
        }

        @Override
        protected String doInBackground(File... params) {
            return OcrAPI.getTextFromFile(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            listener.onExtractionEnded(result);
        }
    }

    public class OcrHyperlinkTask extends AsyncTask<File, Void, URL> {
        private ExtractionListener<URL> listener;

        public OcrHyperlinkTask(ExtractionListener<URL> extListener){
            listener = extListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listener.onExtractionStarted("");
        }

        @Override
        protected URL doInBackground(File... params) {
            String result = OcrAPI.getTextFromFile(params[0]);

            // TODO: trouver le lien dans la chaine

            return null;
        }

        @Override
        protected void onPostExecute(URL result) {
            super.onPostExecute(result);
            listener.onExtractionEnded(result);
        }
    }

    public ImageFileSource(Context context, Uri uri) {
        super(ObjectType.IMAGE, context, uri);
    }

    @Override
    public URL getURL() {
        return null;
    }

    @Override
    public void getURLAsync(HypertextLinkExtractionListener listener) {
        if(listener == null){
            Log.w("ImageFileSource", "ExtractionListener is null");
            return;
        }

        String imagePath = PathResolver.getPathFromPicture(this.getContext(), this.getFileUri());
        File imageFile = new File(imagePath);
        new OcrHyperlinkTask(listener).execute(imageFile);
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public void getTextAsync(TextExtractionListener listener) {
        if(listener == null){
            Log.w("ImageFileSource", "ExtractionListener is null");
            return;
        }

        String imagePath = PathResolver.getPathFromPicture(this.getContext(), this.getFileUri());
        File imageFile = new File(imagePath);
        new OcrTextTask(listener).execute(imageFile);
    }
}

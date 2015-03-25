package com.rokuan.calliope;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.rokuan.calliope.constants.RequestCode;
import com.rokuan.calliope.db.CalliopeSQLiteOpenHelper;
import com.rokuan.calliope.modules.CalliopeModule;
import com.rokuan.calliope.modules.GoogleMaps;
import com.rokuan.calliope.modules.MediaCapture;
import com.rokuan.calliope.view.PictureFileView;
import com.rokuan.calliope.view.VideoFileView;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LEBEAU Christophe on 24/03/2015.
 */
public class HomeActivity extends FragmentActivity implements View.OnTouchListener, RecognitionListener {
    private SpeechRecognizer speech;
    private CalliopeSQLiteOpenHelper db;
    private TextView messageBox;
    private Intent recognizerIntent;

    private ListView contentListView;
    private ViewAdapter viewAdapter;

    private List<CalliopeModule> modules = new ArrayList<CalliopeModule>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "fr");

        findViewById(R.id.speech_activate).setOnTouchListener(this);

        messageBox = (TextView)findViewById(R.id.calliope_message);
        contentListView = (ListView)findViewById(R.id.messages_list);
        viewAdapter = new ViewAdapter(this, new ArrayList<View>());
        viewAdapter.setNotifyOnChange(true);
        contentListView.setAdapter(viewAdapter);

        addModules();
    }

    public void addModules(){
        modules.add(new GoogleMaps(this));
        modules.add(new MediaCapture(this));
    }

    @Override
    public void onPause(){
        super.onPause();

        if(speech != null) {
            speech.destroy();
            speech = null;
        }

        if(db != null) {
            db.close();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        db = new CalliopeSQLiteOpenHelper(this);

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.i("Calliope - Recognition", "startListening");
                speech.startListening(recognizerIntent);
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.i("Calliope - Recognition", "stopListening");
                speech.stopListening();
                return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        try{
            startInterpretationProcess(data.get(0));
        }catch(Exception e){

        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    public void startInterpretationProcess(String text){
        if(text.isEmpty()){
            return;
        }

        String rightPart = text.length() > 1 ? text.substring(1) : "";
        messageBox.setText(Character.toUpperCase(text.charAt(0)) + rightPart);

        try {
            InterpretationObject obj = db.parseSpeech(text);

            if(obj == null){
                // TODO: afficher un message
                return;
            }

            interpret(obj);
        } catch(Exception e){
            // TODO: afficher un message d'erreur
            //e.printStackTrace();
            Log.e("Calliope - startInterpretationProcess", e.toString());
        }
    }

    private void interpret(InterpretationObject object){
        for(CalliopeModule module: modules){
            if(module.canHandle(object) && module.submit(object)){
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bitmap pictureThumbnail = data.getParcelableExtra("data");
            if(data != null) {
                /*Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                // Do other work with full size photo saved in mLocationForPhotos

                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(imageBitmap);
                viewAdapter.add(imageView);*/
                Uri pictureUri = data.getData();
                PictureFileView v = new PictureFileView(this, pictureUri);
                viewAdapter.add(v);
            }

            Log.i("REQUEST_IMAGE_CAPTURE", "OK");
        } else if(requestCode == RequestCode.REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK){
            //Bitmap videoThumbnail = data.getParcelableExtra("data");
            /*Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");*/
            Uri videoUri = data.getData();
            VideoFileView v = new VideoFileView(this, videoUri);
            viewAdapter.add(v);
            Log.i("REQUEST_VIDEO_CAPTURE", "OK");
        }
    }

    class ViewAdapter extends ArrayAdapter<View> {
        public ViewAdapter(Context context, List<View> objects) {
            super(context, R.layout.fragment_main, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            return this.getItem(position);
        }
    }
}

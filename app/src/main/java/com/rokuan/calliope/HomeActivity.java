package com.rokuan.calliope;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.rokuan.calliope.constants.RequestCode;
import com.rokuan.calliope.db.CalliopeSQLiteOpenHelper;
import com.rokuan.calliope.modules.AlarmModule;
import com.rokuan.calliope.modules.CalliopeModule;
import com.rokuan.calliope.modules.GoogleMapsModule;
import com.rokuan.calliope.modules.InterpretationModule;
import com.rokuan.calliope.modules.MediaCaptureModule;
import com.rokuan.calliope.modules.TVModule;
import com.rokuan.calliope.modules.WeatherModule;
import com.rokuan.calliope.source.ImageFileSource;
import com.rokuan.calliope.source.SourceObject;
import com.rokuan.calliope.source.TextSource;
import com.rokuan.calliope.source.VideoFileSource;
import com.rokuan.calliope.view.PictureFileView;
import com.rokuan.calliope.view.VideoFileView;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by LEBEAU Christophe on 24/03/2015.
 */
public class HomeActivity extends FragmentActivity
        implements View.OnTouchListener, RecognitionListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;

    private boolean clientConnected = false;

    //private static final String REQUESTING_LOCATION_UPDATES_KEY = "location_update_request";
    private static final String LOCATION_KEY = "location";

    private SpeechRecognizer speech;
    private CalliopeSQLiteOpenHelper db;
    //private TextView messageBox;
    private EditText messageBox;
    private ImageButton submitText;
    private Intent recognizerIntent;

    private ListView contentListView;
    private ViewAdapter viewAdapter;

    private List<InterpretationModule> modules = new ArrayList<InterpretationModule>();
    private LinkedList<SourceObject> sources = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if(savedInstanceState != null){
            mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
        }

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "fr");


        //messageBox = (TextView)findViewById(R.id.calliope_message);
        messageBox = (EditText)findViewById(R.id.compose_message);
        submitText = (ImageButton)findViewById(R.id.submit_message);
        contentListView = (ListView)findViewById(R.id.messages_list);
        viewAdapter = new ViewAdapter(this, new ArrayList<View>());
        viewAdapter.setNotifyOnChange(true);
        contentListView.setAdapter(viewAdapter);

        findViewById(R.id.speech_activate).setOnTouchListener(this);
        findViewById(R.id.import_image).setOnClickListener(this);
        submitText.setOnClickListener(this);
        submitText.setEnabled(false);
        messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                submitText.setEnabled(!messageBox.getText().toString().isEmpty());
            }
        });

        //buildGoogleApiClient();
        //createLocationRequest();
        //startLocationUpdates();
    }

    private void addModules(){
        modules.add(new GoogleMapsModule(this));
        modules.add(new MediaCaptureModule(this));
        modules.add(new AlarmModule(this));
        modules.add(new WeatherModule(this));
        modules.add(new TVModule(this));
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

        stopLocationUpdates();
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(30000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        if(clientConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    public Location getCurrentLocation(){
        return mCurrentLocation;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        /*savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);*/
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();

        db = new CalliopeSQLiteOpenHelper(this);

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);

        //if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
        addModules();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onBackPressed(){
        this.moveTaskToBack(true);
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
            //startInterpretationProcess(data.get(0));
            //messageBox.setText(data.get(0));
            updateMessageText(data.get(0));
        }catch(Exception e){

        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    private void updateMessageText(String text){
        /*String rightPart = text.length() > 1 ? text.substring(1) : "";
        //messageBox.setText(Character.toUpperCase(text.charAt(0)) + rightPart);
        messageBox.setText(Character.toUpperCase(text.charAt(0)) + rightPart);*/
        messageBox.setText(text);
    }

    public void startInterpretationProcess(String text){
        if(text.isEmpty()){
            return;
        }

        try {
            InterpretationObject obj = db.parseSpeech(text);

            if(obj == null){
                // TODO: afficher un message
                return;
            }

            interpret(obj);
        } catch(Exception e){
            // TODO: afficher un message d'erreur
            e.printStackTrace();
            //Log.e("Calliope - startInterpretationProcess", e.toString());
            //Log.e("Calliope - startInterpretationProcess", e.toString());
        }
    }

    private void interpret(InterpretationObject object){
        for(InterpretationModule module: modules){
            if(module.canHandle(object) && module.submit(object)){
                Log.i("HomeActivity.interpret", "Handled by " + module.toString());
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data == null){
            return;
        }

        if (requestCode == RequestCode.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Uri pictureUri = data.getData();
            addImage(pictureUri);
            Log.i("REQUEST_IMAGE_CAPTURE", "OK");
        } else if(requestCode == RequestCode.REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK){
            Uri videoUri = data.getData();
            addVideo(videoUri);
            Log.i("REQUEST_VIDEO_CAPTURE", "OK");
        } else if(requestCode == RequestCode.REQUEST_IMAGE_PICK && resultCode == RESULT_OK){
            Uri pictureUri = data.getData();
            addImage(pictureUri);
        }
    }

    private void addImage(Uri pictureUri){
        ImageFileSource image = new ImageFileSource(this, pictureUri);
        PictureFileView v = new PictureFileView(this, pictureUri);

        //image.getTextAsync(this);

        sources.add(image);
        viewAdapter.add(v);
    }

    private void addMessage(String text){
        TextSource textSrc = new TextSource(text);
        TextView textView = new TextView(this);

        textView.setText(text);

        sources.add(textSrc);
        viewAdapter.add(textView);
    }

    private void addVideo(Uri videoUri){
        VideoFileSource video = new VideoFileSource(videoUri);
        VideoFileView v = new VideoFileView(this, videoUri);

        sources.add(video);
        viewAdapter.add(v);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.import_image:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Importer image"), RequestCode.REQUEST_IMAGE_PICK);
                break;

            case R.id.submit_message:
                String text = messageBox.getText().toString();
                messageBox.setText("");
                addMessage(text);
                startInterpretationProcess(text);
                break;
        }
    }

    /*@Override
    public void onExtractionStarted(String message) {
        Log.i("TextExtraction", "Extraction started");
    }

    @Override
    public void onExtractionEnded(String value) {
        Log.i("TextExtraction", "Extraction ended with walue '" + value + "'");
        Toast.makeText(this, value, Toast.LENGTH_LONG).show();
    }*/

    public void startProcess(){
        // TODO:
    }

    public void endProcess(){
        // TODO:
    }

    public void insertView(View w){
        viewAdapter.add(w);
    }

    public void setFreeSpeechEnabled(boolean enabled){

    }

    public void requestAdditionalAttribute(String attributeTag, String attribute){

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        clientConnected = true;
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.i("onConnected", "connected");
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    class ViewAdapter extends ArrayAdapter<View> {
        private LayoutInflater inflater;

        public ViewAdapter(Context context, List<View> objects) {
            super(context, R.layout.fragment_main, objects);
            inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            return this.getItem(position);
            /*View mainView = inflater.inflate(R.layout.message_item, parent, false);
            ViewGroup layout = (ViewGroup)mainView.findViewById(R.id.message_item_placeholder);

            layout.removeAllViews();
            layout.addView(this.getItem(position));

            return mainView;*/
        }

        @Override
        public void add(View v){
            View mainView = inflater.inflate(R.layout.message_item, null);
            ViewGroup layout = (ViewGroup)mainView.findViewById(R.id.message_item_placeholder);
            layout.addView(v);
            super.add(mainView);
        }
    }
}

package com.rokuan.calliope;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.animateaddition.AnimateAdditionAdapter;
import com.nhaarman.listviewanimations.util.Insertable;
import com.nineoldandroids.animation.Animator;
import com.rokuan.calliope.constants.RequestCode;
import com.rokuan.calliope.db.CalliopeSQLiteOpenHelper;
import com.rokuan.calliope.modules.AlarmModule;
import com.rokuan.calliope.modules.CalliopeModule;
import com.rokuan.calliope.modules.DecodeModule;
import com.rokuan.calliope.modules.GoogleMapsModule;
import com.rokuan.calliope.modules.InterpretationModule;
import com.rokuan.calliope.modules.LanguageModule;
import com.rokuan.calliope.modules.MediaCaptureModule;
import com.rokuan.calliope.modules.SearchModule;
import com.rokuan.calliope.modules.SystemModule;
import com.rokuan.calliope.modules.TVModule;
import com.rokuan.calliope.modules.WeatherModule;
import com.rokuan.calliope.receiver.IncomingSmsReceiver;
import com.rokuan.calliope.source.AlarmSource;
import com.rokuan.calliope.source.AudioFileSource;
import com.rokuan.calliope.source.CommandSource;
import com.rokuan.calliope.source.ForecastSource;
import com.rokuan.calliope.source.ImageFileSource;
import com.rokuan.calliope.source.SmsSource;
import com.rokuan.calliope.source.SourceObject;
import com.rokuan.calliope.source.SourceObjectProvider;
import com.rokuan.calliope.source.TVListingsSource;
import com.rokuan.calliope.source.TextSource;
import com.rokuan.calliope.source.TranslationSource;
import com.rokuan.calliope.source.VideoFileSource;
import com.rokuan.calliope.source.WeatherSource;
import com.rokuan.calliope.view.AlarmView;
import com.rokuan.calliope.view.AudioFileView;
import com.rokuan.calliope.view.CommandView;
import com.rokuan.calliope.view.ForecastView;
import com.rokuan.calliope.view.PictureFileView;
import com.rokuan.calliope.view.SmsView;
import com.rokuan.calliope.view.StringView;
import com.rokuan.calliope.view.TVListingView;
import com.rokuan.calliope.view.TranslationView;
import com.rokuan.calliope.view.VideoFileView;
import com.rokuan.calliope.view.WeatherView;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

/**
 * Created by LEBEAU Christophe on 24/03/2015.
 */
public class HomeActivity extends ActionBarActivity
        implements View.OnTouchListener, RecognitionListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, CompoundButton.OnCheckedChangeListener, SourceObjectProvider {
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;

    private boolean clientConnected = false;

    //private static final String REQUESTING_LOCATION_UPDATES_KEY = "location_update_request";
    private static final String LOCATION_KEY = "location";

    public static final int SPEECH = 0;
    public static final int TEXT = 1;
    public static final int PROGRESS = 2;

    private static final int MESSAGEBOX_ANIMATION_DURATION = 500;

    private SpeechRecognizer speech;
    private CalliopeSQLiteOpenHelper db;
    private Intent recognizerIntent;
    private SourceAdapter sourceAdapter;

    private YoYo.AnimationComposer showMessageLayoutAnimation;
    private YoYo.AnimationComposer hideMessageLayoutAnimation;

    private List<InterpretationModule> modules = new ArrayList<InterpretationModule>();
    private LinkedList<SourceObject> sources = new LinkedList<>();
    private List<BroadcastReceiver> receivers = new ArrayList<>();

    private boolean freeSpeechModeActivated = true;

    @InjectViews({ R.id.speech_frame, R.id.text_frame, R.id.progress_frame }) List<View> frames;
    @InjectView(R.id.speech_activate) protected ActionButton speechButton;
    @InjectView(R.id.compose_message) protected EditText messageBox;
    @InjectView(R.id.submit_message) protected ImageButton submitText;
    @InjectView(R.id.messages_list) protected DynamicListView contentListView;
    @InjectView(R.id.text_form) protected View textFormLayout;
    @InjectView(R.id.add_text_source) protected Button addTextButton;
    @InjectView(R.id.text_source_content) protected EditText textContent;

    public static final ButterKnife.Action<View> HIDE = new ButterKnife.Action<View>() {
        @Override
        public void apply(View view, int index) {
            view.setVisibility(View.INVISIBLE);
        }
    };

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

        ButterKnife.inject(this);
        ButterKnife.apply(frames, HIDE);
        frames.get(SPEECH).setVisibility(View.VISIBLE);

        sourceAdapter = new SourceAdapter(this, sources);
        sourceAdapter.setNotifyOnChange(true);
        contentListView.disableDragAndDrop();
        contentListView.disableSwipeToDismiss();
        SwingBottomInAnimationAdapter animAdapter = new SwingBottomInAnimationAdapter(sourceAdapter);
        animAdapter.setAbsListView(contentListView);
        assert animAdapter.getViewAnimator() != null;
        animAdapter.getViewAnimator().setInitialDelayMillis(100);
        contentListView.setAdapter(animAdapter);

        //findViewById(R.id.speech_activate).setOnTouchListener(this);
        speechButton.setOnTouchListener(this);
        ToggleButton speechModeToggle = (ToggleButton)findViewById(R.id.speech_mode);
        speechModeToggle.setOnCheckedChangeListener(this);
        speechModeToggle.setChecked(freeSpeechModeActivated);
        submitText.setEnabled(false);
        messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                submitText.setEnabled(!messageBox.getText().toString().isEmpty());
            }
        });

        addTextButton.setEnabled(false);
        textContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                addTextButton.setEnabled(!textContent.getText().toString().isEmpty());
            }
        });

        hideMessageLayoutAnimation = YoYo.with(Techniques.SlideOutLeft).duration(MESSAGEBOX_ANIMATION_DURATION);
        showMessageLayoutAnimation = YoYo.with(Techniques.SlideInRight).duration(MESSAGEBOX_ANIMATION_DURATION);

        addModules();
        //addReceivers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        } else*/
        if(id == R.id.action_import_picture){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Importer image"), RequestCode.REQUEST_IMAGE_PICK);
            return true;
        } else if(id == R.id.action_import_text) {
            textContent.setText("");
            textFormLayout.setVisibility(textFormLayout.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }

    private void addModules(){
        modules.add(new GoogleMapsModule(this));
        modules.add(new MediaCaptureModule(this));
        modules.add(new AlarmModule(this));
        modules.add(new WeatherModule(this));
        modules.add(new TVModule(this));
        modules.add(new SearchModule(this));
        modules.add(new SystemModule(this));
        //modules.add(new DecodeModule(this));
        modules.add(new LanguageModule(this));
    }

    private void addReceivers(){
        receivers.add(new IncomingSmsReceiver(this));
    }

    private void registerReceivers(){
        this.registerReceiver(receivers.get(0), new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    }

    private void unregisterReceivers(){
        for(int i=0; i<receivers.size(); i++){
            this.unregisterReceiver(receivers.get(i));
        }
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
        //unregisterReceivers();
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(30000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
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

    protected void onSaveInstanceState(Bundle savedInstanceState) {
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


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        //registerReceivers();
    }

    @Override
    public void onBackPressed(){
        this.moveTaskToBack(true);
    }

    @OnClick(R.id.submit_message)
    public void submitSpeech(){
        String text = messageBox.getText().toString();
        messageBox.setText("");
        hideMessageBox(true, text);
    }

    @OnClick(R.id.cancel_message)
    public void cancelSpeech(){
        messageBox.setText("");
        hideMessageBox(false, null);
    }

    @OnClick(R.id.add_text_source)
    public void submitTextSource(){
        // TODO: ajouter une animation
        textFormLayout.setVisibility(View.INVISIBLE);

        String text = textContent.getText().toString();
        textContent.setText("");

        if(!text.isEmpty()){
            addText(text);
        }
    }

    /*@OnClick(R.id.import_image)
    public void importImageFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Importer image"), RequestCode.REQUEST_IMAGE_PICK);
    }*/

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.i("Calliope - Recognition", "startListening");
                speechButton.setState(ActionButton.State.PRESSED);
                speech.startListening(recognizerIntent);
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.i("Calliope - Recognition", "stopListening");
                speechButton.setState(ActionButton.State.NORMAL);
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
            //updateMessageText(data.get(0));
            checkSpeechInterpretation(data.get(0));
        }catch(Exception e){

        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    private void checkSpeechInterpretation(String text){
        /*String rightPart = text.length() > 1 ? text.substring(1) : "";
        //messageBox.setText(Character.toUpperCase(text.charAt(0)) + rightPart);
        messageBox.setText(Character.toUpperCase(text.charAt(0)) + rightPart);*/
        if(freeSpeechModeActivated) {
            startInterpretationProcess(text);
        } else {
            messageBox.setText(text);
            showMessageBox();
        }
    }

    private void showMessageBox(){
        frames.get(SPEECH).setVisibility(View.INVISIBLE);
        frames.get(TEXT).setVisibility(View.VISIBLE);
        showMessageLayoutAnimation.playOn(frames.get(TEXT));
    }

    private void hideMessageBox(boolean shouldExecute, String text){
        final String message = text;

        if(shouldExecute) {
            hideMessageLayoutAnimation.withListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    frames.get(TEXT).setVisibility(View.INVISIBLE);
                    frames.get(SPEECH).setVisibility(View.VISIBLE);
                    //addCommand(message);
                    startInterpretationProcess(message);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).playOn(frames.get(TEXT));
        } else {
            //frames.get(TEXT).setVisibility(View.INVISIBLE);
            hideMessageLayoutAnimation.withListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    frames.get(SPEECH).setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).playOn(frames.get(TEXT));
        }
    }

    public void startInterpretationProcess(String text){
        if(text.isEmpty()){
            return;
        }

        addCommand(text);

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
        } else if(requestCode == RequestCode.REQUEST_AUDIO_CAPTURE && resultCode == RESULT_OK){
            Uri soundUri = data.getData();
            addAudio(soundUri);
        } else if(requestCode == RequestCode.REQUEST_IMAGE_PICK && resultCode == RESULT_OK){
            Uri pictureUri = data.getData();
            addImage(pictureUri);
        }
    }

    private void addImage(Uri pictureUri){
        ImageFileSource image = new ImageFileSource(this, pictureUri);
        addSource(image);
    }

    private void addText(String text){
        TextSource txtSrc = new TextSource(text);
        addSource(txtSrc);
    }

    private void addCommand(String cmd){
        CommandSource cmdSrc = new CommandSource(cmd);
        addSource(cmdSrc);
    }

    private void addVideo(Uri videoUri){
        VideoFileSource video = new VideoFileSource(videoUri);
        addSource(video);
    }

    private void addAudio(Uri audioUri){
        AudioFileSource audio = new AudioFileSource(audioUri);
        addSource(audio);
    }

    @Override
    public SourceObject getLastSourceOfType(SourceObject.ObjectType type) {
        try{
            for(int i=sources.size() - 1; i>=0; i--){
                SourceObject item = sources.get(i);

                if(item.getType() == type){
                    return sources.get(i);
                }
            }
        }catch(Exception e){
            return null;
        }

        return null;
    }

    @Override
    public void addSource(SourceObject src){
        contentListView.insert(sourceAdapter.getCount(), src);
    }

    public void startProcess(){
        // TODO:
        ButterKnife.apply(frames, HIDE);
        frames.get(PROGRESS).setVisibility(View.VISIBLE);
    }

    public void endProcess(){
        // TODO:
        ButterKnife.apply(frames, HIDE);
        frames.get(SPEECH).setVisibility(View.VISIBLE);
    }

    public void setFreeSpeechEnabled(boolean enabled){
        freeSpeechModeActivated = enabled;
    }

    public void requestAdditionalAttribute(String attributeTag, String attribute){

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        clientConnected = true;
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setFreeSpeechEnabled(isChecked);
    }

    class SourceAdapter extends ArrayAdapter<SourceObject> implements Insertable<SourceObject> {
        private LayoutInflater inflater;

        public SourceAdapter(Context context, List<SourceObject> objects) {
            super(context, R.layout.fragment_main, objects);
            inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            SourceObject.ObjectType type = SourceObject.ObjectType.values()[this.getItemViewType(position)];
            SourceObject item = this.getItem(position);

            View mainView = convertView;

            if(convertView == null){
                mainView = inflater.inflate(R.layout.message_item, parent, false);
            }

            ViewGroup placeHolder = (ViewGroup)mainView.findViewById(R.id.message_item_placeholder);
            View itemView;

            placeHolder.removeAllViews();

            switch(type){
                case ALARM:
                    itemView = new AlarmView(this.getContext(), ((AlarmSource)item).getTime());
                    break;
                case COMMAND:
                    itemView = new CommandView(this.getContext(), ((CommandSource)item).getCommand());
                    break;
                case TEXT:
                    itemView = new StringView(this.getContext(), ((TextSource)item).getText());
                    break;
                case IMAGE:
                    itemView = new PictureFileView(this.getContext(), ((ImageFileSource)item).getFileUri());
                    break;
                case VIDEO:
                    itemView = new VideoFileView(this.getContext(), ((VideoFileSource)item).getFileUri());
                    break;
                case AUDIO:
                    itemView = new AudioFileView(this.getContext(), ((AudioFileSource)item).getFileUri());
                    break;
                case WEATHER:
                    itemView = new WeatherView(this.getContext(), ((WeatherSource)item).getWeatherData());
                    break;
                case FORECAST:
                    itemView = new ForecastView(this.getContext(), ((ForecastSource)item).getForecastData());
                    break;
                case TV_LISTING:
                    itemView = new TVListingView(this.getContext(), ((TVListingsSource)item).getProgramList());
                    break;
                case TRANSLATION:
                    itemView = new TranslationView(this.getContext(), ((TranslationSource)item).getTranslationData());
                    break;
                case SMS:
                    itemView = new SmsView(this.getContext(), ((SmsSource)item).getData());
                    break;
                case LINK:
                case PERSON:
                case WORD:
                case EVENT:
                case MAIL:
                default:
                    return null;
            }

            placeHolder.addView(itemView);
            return mainView;
        }

        @Override
        public int getViewTypeCount(){
            return SourceObject.ObjectType.values().length;
        }

        @Override
        public int getItemViewType(int position){
            return this.getItem(position).getType().ordinal();
        }

        @Override
        public void add(int i, SourceObject src) {
            this.add(src);
        }
    }
}

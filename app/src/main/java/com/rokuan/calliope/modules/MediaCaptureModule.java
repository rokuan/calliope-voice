package com.rokuan.calliope.modules;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.rokuan.calliope.HomeActivity;
import com.rokuan.calliope.constants.RequestCode;
import com.rokuan.calliopecore.sentence.Action;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by LEBEAU Christophe on 24/03/2015.
 */
public class MediaCaptureModule extends ContextModule {
    //private static final String PICTURES_PATH = "calliope/pictures/";
    //private static final String MEDIA_REGEX = "(microphone|caméscope|caméra|((photo|vidéo)(s?)))";
    private static final String MEDIA_REGEX = "(microphone|caméscope|caméra|photo|vidéo)";

    public MediaCaptureModule(Context c) {
        super(c);
    }

    @Override
    public boolean canHandle(InterpretationObject object) {
        return object.getDescription().matches(".*:" + MEDIA_REGEX);
    }

    @Override
    public boolean submit(InterpretationObject object) {
        if(object.what != null) {
            if(object.what.object != null) {
                if(object.what.object.startsWith("photo")) {
                    switch ((Action.VerbAction) object.action) {
                        case TAKE:
                        case TAKE_AGAIN:
                            capturePhoto();
                            return true;

                        case OPEN:
                        case OPEN_AGAIN:
                        case DISPLAY:
                        case DISPLAY_SHOW:
                        case SHOW:
                            openCameraOnPictureMode();
                            return true;
                    }
                } else if(object.what.object.startsWith("vidéo")){
                    switch((Action.VerbAction)object.action){
                        case TAKE:
                        case TAKE_AGAIN:
                        case SAVE__RECORD:
                        case RECORD_VIDEO:
                            captureVideo();
                            return true;
                    }
                } else if(object.what.object.startsWith("caméra")){
                    switch ((Action.VerbAction)object.action){
                        case START:
                        case OPEN:
                        case OPEN_AGAIN:
                        case DISPLAY:
                        case DISPLAY_SHOW:
                        case SHOW:
                            //openCameraOnVideoMode();
                            captureVideo();
                            return true;
                    }
                }
            }
        }

        return false;
    }

    private void capturePhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(this.getContext().getPackageManager()) != null) {
            ((HomeActivity)this.getContext()).startActivityForResult(intent, RequestCode.REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this.getContext(), "Aucune application ne prend en charge cette action", Toast.LENGTH_SHORT).show();
        }
    }

    private void captureVideo(){
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(this.getContext().getPackageManager()) != null) {
            ((HomeActivity)this.getContext()).startActivityForResult(takeVideoIntent, RequestCode.REQUEST_VIDEO_CAPTURE);
        } else {
            Toast.makeText(this.getContext(), "Aucune application ne prend en charge cette action", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCameraOnPictureMode(){

    }

    private void openCameraOnVideoMode(){

    }
}

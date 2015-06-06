package com.rokuan.calliope.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rokuan.calliope.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by LEBEAU Christophe on 25/03/2015.
 */
public class PictureFileView extends LinearLayout {
    private Uri pictureUri;

    public PictureFileView(Context context, Uri u) {
        super(context);
        pictureUri = u;

        initPictureView();
    }

    private void initPictureView(){
        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_picture, this);
        ImageView imageView = (ImageView)findViewById(R.id.view_picture_content);
        /*try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), pictureUri);
            imageView.setImageBitmap(bmp);
        } catch (IOException e) {
            // TODO:
            e.printStackTrace();
        }*/
        Picasso.with(this.getContext()).load(pictureUri).into(imageView);
    }
}

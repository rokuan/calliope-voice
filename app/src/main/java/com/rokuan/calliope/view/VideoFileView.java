package com.rokuan.calliope.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.rokuan.calliope.R;

/**
 * Created by LEBEAU Christophe on 25/03/2015.
 */
public class VideoFileView extends LinearLayout implements View.OnClickListener {
    private Uri videoUri;
    private VideoView player;
    private ImageButton playButton;
    private ImageButton pauseButton;

    public VideoFileView(Context context, Uri u) {
        super(context);
        videoUri = u;
        initVideoView();
    }

    private void initVideoView(){
        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.view_video, this);

        player = (VideoView)findViewById(R.id.view_video_content);
        player.setOnClickListener(this);
        player.setVideoURI(videoUri);

        playButton = (ImageButton)findViewById(R.id.view_video_play);
        pauseButton = (ImageButton)findViewById(R.id.view_video_pause);
        playButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.view_video_play:
                playButton.setVisibility(INVISIBLE);

                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        playButton.setVisibility(VISIBLE);
                    }
                });
                player.start();
                break;

            case R.id.view_video_pause:
                pauseButton.setVisibility(INVISIBLE);
                player.resume();
                break;

            case R.id.view_video_content:
                player.pause();
                pauseButton.setVisibility(VISIBLE);
                break;
        }
    }
}

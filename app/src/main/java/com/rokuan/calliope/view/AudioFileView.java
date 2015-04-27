package com.rokuan.calliope.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.media.session.MediaController;
import android.net.Uri;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.rokuan.calliope.R;

import java.io.IOException;
import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by LEBEAU Christophe on 25/03/2015.
 */
public class AudioFileView extends LinearLayout {
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("");

    private Uri audioUri;
    //private MediaController player;
    private MediaPlayer player;

    @InjectView(R.id.view_audio_play_pause) protected ImageButton playPauseButton;
    @InjectView(R.id.view_audio_stop) protected ImageButton stopButton;
    @InjectView(R.id.view_audio_progress) protected SeekBar progress;
    @InjectView(R.id.view_audio_time) protected TextView durationTextView;

    public AudioFileView(Context context, Uri u) {
        super(context);
        audioUri = u;
        initAudioFileView();
    }

    private void initAudioFileView(){
        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_audio, this);

        ButterKnife.inject(this);

        player = new MediaPlayer();

        try {
            player.setDataSource(this.getContext(), audioUri);
            // TODO: accorder la seekbar avec l'avancement
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

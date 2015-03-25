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

/**
 * Created by LEBEAU Christophe on 25/03/2015.
 */
public class AudioFileView extends LinearLayout {
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("");

    private Uri audioUri;
    private ImageButton playPauseButton;
    private ImageButton stopButton;
    private SeekBar progress;
    private TextView durationTextView;
    //private MediaController player;
    private MediaPlayer player;

    public AudioFileView(Context context, Uri u) {
        super(context);
        audioUri = u;
        initAudioFileView();
    }

    private void initAudioFileView(){
        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.view_audio, this);

        playPauseButton = (ImageButton)findViewById(R.id.view_audio_play_pause);
        stopButton = (ImageButton)findViewById(R.id.view_audio_stop);
        progress = (SeekBar)findViewById(R.id.view_audio_progress);
        durationTextView = (TextView)findViewById(R.id.view_audio_time);

        try {
            player.setDataSource(this.getContext(), audioUri);
            // TODO: accorder la seekbar avec l'avancement
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

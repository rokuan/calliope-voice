package com.rokuan.calliope.api.kimonotv;

import android.graphics.Bitmap;

import com.rokuan.calliope.utils.RemoteData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LEBEAU Christophe on 23/04/2015.
 */
public class TVProgram {
    private int channelId;
    private int channelNumber;
    private String channelName;
    private Bitmap channelLogo;
    private String time;
    private String title;
    private Bitmap image;

    public static TVProgram buildFromJSON(JSONObject json) throws JSONException {
        TVProgram program = new TVProgram();

        program.channelId = json.getInt("channel_id");
        program.channelNumber = json.getInt("channel_number");
        program.channelName = json.getString("channel");
        program.channelLogo = RemoteData.getBitmapFromURL(json.getString("logo"));
        program.title = json.getString("title");
        program.time = json.getString("time");
        program.image = RemoteData.getBitmapFromURL(json.getString("image"));

        return program;
    }

    public int getChannelId() {
        return channelId;
    }

    public int getChannelNumber() {
        return channelNumber;
    }

    public String getChannelName() {
        return channelName;
    }

    public Bitmap getChannelLogo() {
        return channelLogo;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getImage() {
        return image;
    }
}

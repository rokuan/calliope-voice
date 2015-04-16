package com.rokuan.calliope.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.rokuan.calliope.R;

import java.util.List;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class TVListingView extends LinearLayout {
    private static final String API_ADDRESS = "https://www.kimonolabs.com/api/afxopnoq?&apikey=6e1c9495fe8d4f9921da587e4d007465&kimmodify=1";

    private ListView listings;
    private View loadingFrame;

    private int channelNumber = -1;
    private String channelName = null;

    public TVListingView(Context context) {
        super(context);
        initTVListingView();
    }

    private void initTVListingView(){
        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.view_tvlisting, this);

        listings = (ListView)this.findViewById(R.id.view_tvlisting_list);
        loadingFrame = this.findViewById(R.id.view_tvlisting_loading_frame);
    }

    private void startLoading(){
        loadingFrame.setVisibility(VISIBLE);
    }

    private void endLoading(boolean success, List<String> results){

    }
}

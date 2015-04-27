package com.rokuan.calliope.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.rokuan.calliope.R;
import com.rokuan.calliope.api.kimonotv.TVProgram;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class TVListingView extends LinearLayout {
    private ListView listings;
    private Spinner channelSelection;
    private ArrayAdapter<String> spinnerAdapter;
    private List<TVProgram> programs;
    private TVProgramAdapter programsAdapter;

    public TVListingView(Context context, List<TVProgram> ps) {
        super(context);
        programs = ps;
        initTVListingView();
    }

    private void initTVListingView(){
        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.view_tvlisting, this);

        channelSelection = (Spinner)this.findViewById(R.id.view_tvlisting_channel);
        listings = (ListView)this.findViewById(R.id.view_tvlisting_list);

        Set<String> channelNames = new LinkedHashSet<>();

        for(TVProgram prog: programs){
            channelNames.add(prog.getChannelName());
        }

        String[] channelNamesArray = new String[channelNames.size()];
        channelNames.toArray(channelNamesArray);

        spinnerAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, channelNamesArray);
        programsAdapter = new TVProgramAdapter(this.getContext(), programs);
        channelSelection.setAdapter(spinnerAdapter);
        listings.setAdapter(programsAdapter);

        channelSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String channelName = spinnerAdapter.getItem(position);
                programsAdapter.getFilter().filter(channelName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        channelSelection.setSelection(0);
    }

    class TVProgramAdapter extends ArrayAdapter<TVProgram> {
        private LayoutInflater inflater;
        private List<TVProgram> allPrograms;
        private List<TVProgram> filteredPrograms;
        private Filter filter;

        public TVProgramAdapter(Context context, List<TVProgram> objects) {
            super(context, R.layout.view_tvlisting_item, objects);
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            allPrograms = objects;
            filteredPrograms = new ArrayList<>();
        }

        @Override
        public TVProgram getItem(int position){
            return filteredPrograms.get(position);
        }

        @Override
        public int getCount(){
            return filteredPrograms.size();
        }

        @Override
        public Filter getFilter(){
            if(filter == null) {
                filter = new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults results = new FilterResults();

                        if(constraint != null && constraint.toString().length() > 0){
                            ArrayList<TVProgram> programsOnChannel = new ArrayList<>();

                            for(int i=0; i<allPrograms.size(); i++){
                                TVProgram prog = allPrograms.get(i);

                                if(prog.getChannelName().equals(constraint)){
                                    programsOnChannel.add(prog);
                                }
                            }

                            results.count = programsOnChannel.size();
                            results.values = programsOnChannel;
                        }

                        return results;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        filteredPrograms.clear();
                        filteredPrograms.addAll((ArrayList<TVProgram>) results.values);
                        notifyDataSetChanged();
                    }
                };
            }

            return filter;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View v = convertView;

            if(v == null){
                v = inflater.inflate(R.layout.view_tvlisting_item, parent, false);
            }

            TVProgram program = this.getItem(position);

            TextView programName = (TextView)v.findViewById(R.id.view_tvlisting_item_title);
            TextView programTime = (TextView)v.findViewById(R.id.view_tvlisting_item_time);
            ImageView programImage = (ImageView)v.findViewById(R.id.view_tvlisting_item_image);
            ImageView channelLogo = (ImageView)v.findViewById(R.id.view_tvlisting_item_channel_logo);

            programName.setText(program.getTitle());
            programTime.setText(program.getTime());
            //programImage.setImageBitmap(program.getImage());
            Picasso.with(this.getContext()).load(program.getImageURL()).into(programImage);
            Picasso.with(this.getContext()).load(program.getChannelLogoURL()).into(channelLogo);
            //channelLogo.setImageBitmap(program.getChannelLogo());

            return v;
        }
    }
}

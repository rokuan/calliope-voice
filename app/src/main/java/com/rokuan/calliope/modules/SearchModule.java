package com.rokuan.calliope.modules;

import android.content.Context;
import android.content.Intent;

import com.google.android.youtube.player.YouTubeIntents;
import com.rokuan.calliope.HomeActivity;
import com.rokuan.calliope.utils.TextUtils;
import com.rokuan.calliopecore.sentence.Action;
import com.rokuan.calliopecore.sentence.structure.ComplementObject;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.NominalGroup;

import java.text.Normalizer;

/**
 * Created by LEBEAU Christophe on 12/04/2015.
 */
public class SearchModule extends CalliopeModule {
    private static final String ITEM_CONTENT_REGEX = "((vidéo|chanson|musique|image)(s?))";

    public SearchModule(HomeActivity a) {
        super(a);
    }

    @Override
    public boolean canHandle(InterpretationObject object) {
        if(object.getType() == InterpretationObject.RequestType.ORDER){
            switch((Action.VerbAction)object.action){
                case FIND:
                case SEARCH:
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean submit(InterpretationObject object) {
        if(object.getType() == InterpretationObject.RequestType.ORDER){
            if(object.what != null && object.what.getType() == NominalGroup.GroupType.NOMINAL_GROUP){
                ComplementObject compl = (ComplementObject)object.what;

                if(TextUtils.removeAllAccents(compl.object).matches("video(s?)")){
                    if(compl.of != null){
                        Intent youtubeSearch = YouTubeIntents.createSearchIntent(this.getActivity(), compl.of.object);
                        this.getActivity().startActivity(youtubeSearch);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

package com.rokuan.calliope.modules;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.rokuan.calliopecore.sentence.Action;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.OrderObject;
import com.rokuan.calliopecore.sentence.structure.QuestionObject;

/**
 * Created by LEBEAU Christophe on 24/03/2015.
 */
public class GoogleMaps extends IntentModule {
    private static final String PLACES_REGEX = "((restaurant|cinéma)(s?))";
    private static final String MAP_REGEX = "(carte|map)";

    public GoogleMaps(Context context){
        super(context);
    }

    @Override
    public boolean canHandle(InterpretationObject object) {
        if(object.what != null && object.what.object != null
                && (object.what.object.matches(PLACES_REGEX) || object.what.object.matches(MAP_REGEX))){
            return true;
        }

        return false;
    }

    @Override
    public boolean submit(InterpretationObject object) {
        if(object.getType() == InterpretationObject.RequestType.QUESTION){
            QuestionObject question = (QuestionObject)object;

            if(question.qType == QuestionObject.QuestionType.WHAT && question.action == Action.VerbAction.BE) {
                OrderObject order = new OrderObject();

                order.action = Action.VerbAction.FIND;
                order.what = object.what;

                return this.submit(order);
            }
        } else if(object.getType() == InterpretationObject.RequestType.ORDER) {
            switch ((Action.VerbAction) object.action) {
                case DISPLAY_SHOW:
                case DISPLAY:
                case GIVE:
                case SHOW:
                case FIND:
                case SEARCH:
                    if (object.what != null) {
                        if (object.what.object.matches(PLACES_REGEX)) {
                            String placeType = getTypeFromString(object.what.object);
                            Uri gmmIntentUri = Uri.parse("geo:0,0")
                                    .buildUpon()
                                    .appendQueryParameter("q", placeType)
                                    .build();
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            this.getContext().startActivity(mapIntent);
                            return true;
                        } else if (object.what.object.matches(MAP_REGEX)) {
                            Uri gmmIntentUri = Uri.parse("geo:0,0");
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            this.getContext().startActivity(mapIntent);
                            return true;
                        }
                    }
                    break;

                case PLACE:
                    break;
            }
        }

        return false;
    }

    private static String getTypeFromString(String type){
        if(type.startsWith("restaurant")){
            return "restaurants";
        } else if(type.startsWith("cinéma")){
            return "cinemas";
        }

        return "";
    }
}

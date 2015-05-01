package com.rokuan.calliope.modules;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.rokuan.calliope.HomeActivity;
import com.rokuan.calliopecore.sentence.Action;
import com.rokuan.calliopecore.sentence.structure.ComplementObject;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.NominalGroup;
import com.rokuan.calliopecore.sentence.structure.OrderObject;
import com.rokuan.calliopecore.sentence.structure.QuestionObject;
import com.rokuan.calliopecore.sentence.structure.data.place.MonumentObject;
import com.rokuan.calliopecore.sentence.structure.data.place.PlaceObject;

/**
 * Created by LEBEAU Christophe on 24/03/2015.
 */
public class GoogleMapsModule extends CalliopeModule {
    private static final String PLACES_REGEX = "((restaurant|cinéma)(s?))";
    private static final String MAP_REGEX = "(carte|map)";

    public GoogleMapsModule(HomeActivity a){
        super(a);
    }

    @Override
    public boolean canHandle(InterpretationObject object) {
        if(object.what != null && object.what.getType() == NominalGroup.GroupType.NOMINAL_GROUP){
            ComplementObject compl = (ComplementObject)object.what;

            if(compl.object.matches(PLACES_REGEX) || compl.object.matches(MAP_REGEX)){
                return true;
            }
        }

        if(object.getType() == InterpretationObject.RequestType.QUESTION){
            QuestionObject question = (QuestionObject)object;

            if(question.qType == QuestionObject.QuestionType.HOW){
                // TODO: ajouter le verbe "se rendre"
                return question.action == Action.VerbAction.GO;
            }

            if(question.qType == QuestionObject.QuestionType.WHERE){
                switch((Action.VerbAction)question.action) {
                    case PLACE:
                    case FIND:
                        return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean submit(InterpretationObject object) {
        if(object.getType() == InterpretationObject.RequestType.QUESTION){
            QuestionObject question = (QuestionObject)object;

            if(question.qType == QuestionObject.QuestionType.WHAT && question.action == Action.VerbAction.BE){
                OrderObject order = new OrderObject();

                order.action = Action.VerbAction.FIND;
                order.what = object.what;

                return this.submit(order);
            }

            if(question.qType == QuestionObject.QuestionType.HOW && question.action == Action.VerbAction.GO){
                if(question.where != null){
                    showNavigation(question.where, question.how);
                    return true;
                }

                return false;
            }


        } else if(object.getType() == InterpretationObject.RequestType.ORDER) {
            switch ((Action.VerbAction) object.action) {
                case DISPLAY_SHOW:
                case DISPLAY:
                case GIVE:
                case SHOW:
                case FIND:
                case SEARCH:
                    if (object.what != null && object.what.getType() == NominalGroup.GroupType.NOMINAL_GROUP) {
                        ComplementObject compl = (ComplementObject)object.what;

                        if (compl.object.matches(PLACES_REGEX)) {
                            String placeType = getTypeFromString(compl.object);
                            Uri gmmIntentUri = Uri.parse("geo:0,0")
                                    .buildUpon()
                                    .appendQueryParameter("q", placeType)
                                    .build();
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            this.getActivity().startActivity(mapIntent);
                            return true;
                        } else if (compl.object.matches(MAP_REGEX)) {
                            Uri gmmIntentUri = Uri.parse("geo:0,0");
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            this.getActivity().startActivity(mapIntent);
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

    private void showNavigation(PlaceObject place, String meanOfTransport){
        String queryString = "google.navigation:q=";

        if(place.getType() == PlaceObject.PlaceType.MONUMENT){
            MonumentObject monument = (MonumentObject)place;
            queryString += monument.name;

            if(monument.city != null || monument.country != null){
                queryString += ",";

                if(monument.city != null){
                    queryString += monument.city;
                }

                if(monument.country != null){
                    if(queryString.charAt(queryString.length() - 1) != ','){
                        queryString += " ";
                    }

                    queryString += monument.country;
                }
            }

            String mode = getMeanOfTransportFromString(meanOfTransport);

            if(mode != null){
                queryString += "&mode=" + mode;
            }
        }

        Uri gmmIntentUri = Uri.parse(queryString);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        this.getActivity().startActivity(mapIntent);
    }

    private static String getTypeFromString(String type){
        if(type.startsWith("restaurant")){
            return "restaurants";
        } else if(type.startsWith("cinéma")){
            return "cinemas";
        }

        return "";
    }

    private static String getMeanOfTransportFromString(String mean){
        if(mean == null){
            return null;
        }

        if(mean.equals("voiture")){
            return "d";
        } else if(mean.equals("pied")){
            return "w";
        } else if(mean.equals("vélo")){
            return "b";
        }

        return null;
    }
}

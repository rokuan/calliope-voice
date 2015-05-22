package com.rokuan.calliope.source;

import android.content.Context;

/**
 * Created by LEBEAU Christophe on 01/04/2015.
 */
public abstract class SourceObject {
    public enum ObjectType {
        COMMAND,
        TEXT,
        IMAGE,
        AUDIO,
        VIDEO,
        LINK,
        ALARM,
        WEATHER,
        FORECAST,
        TV_LISTING,
        PERSON,
        WORD,
        EVENT,
        SMS,
        MAIL
    }

    private Context context;
    private ObjectType type;

    protected SourceObject(ObjectType ty){
        type = ty;
    }

    protected SourceObject(ObjectType ty, Context c){
        type = ty;
        context = c;
    }

    public ObjectType getType(){
        return type;
    }
    public Context getContext() { return context; };

    public static ObjectType getSourceType(String str){
        if(str.matches("(photo.* | image(s?))")){
            return ObjectType.IMAGE;
        } else if(str.matches("(musique(s?) | chanson(s?))")){
            return ObjectType.AUDIO;
        } else if(str.matches("(vidéo(s?))")){
            return  ObjectType.VIDEO;
        } else if(str.matches("(adresse | lien)")){
            return ObjectType.LINK;
        } else if(str.matches("(SMS | sms | message)")){
            return ObjectType.SMS;
        } else if(str.matches("(mail(s?) | e-mail(s?))")){
            return ObjectType.MAIL;
        } else if(str.matches("(texte | phrase)")){
            return ObjectType.TEXT;
        }

        // TODO:

        return null;
    }
}

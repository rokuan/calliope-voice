package com.rokuan.calliope.db;

import android.database.Cursor;

import com.rokuan.calliopecore.sentence.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LEBEAU Christophe on 07/03/2015.
 */
public class CalliopeWord extends Word {
    public CalliopeWord(String v, List<WordType> ts){
        super(v, ts);
    }

    public static CalliopeWord buildFromCursor(Cursor result){
        String value = result.getString(0);

        String typesList = result.getString(1);
        String[] wordTypes = typesList.split(",");

        List<WordType> types = new ArrayList<WordType>(wordTypes.length);

        for(String wType: wordTypes){
            types.add(WordType.valueOf(wType));
        }

        return new CalliopeWord(value, types);
    }

    @Override
    public String toString(){
        StringBuilder result = new StringBuilder(this.getValue());

        if(this.getVerbInfo() != null){
            result.append(" (");
            result.append(this.getVerbInfo().getVerb().getVerb());
            result.append(')');
        }

        return result.toString();
    }
}

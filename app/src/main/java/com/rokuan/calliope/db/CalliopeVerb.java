package com.rokuan.calliope.db;

import android.database.Cursor;

import com.rokuan.calliopecore.sentence.Action;
import com.rokuan.calliopecore.sentence.Verb;

/**
 * Created by LEBEAU Christophe on 07/03/2015.
 */
public class CalliopeVerb extends Verb<Action.VerbAction> {
    public static Verb buildFromCursor(Cursor result){
        CalliopeVerb v = new CalliopeVerb();

        //v.id = result.getInt(0);
        v.verb = result.getString(0);
        v.action = Action.VerbAction.valueOf(result.getString(1));
        //v.group = result.getInt(3);
        v.auxiliary = (result.getInt(2) != 0);

        return v;
    }
}

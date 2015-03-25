package com.rokuan.calliope.db;

import android.database.Cursor;

import com.rokuan.calliopecore.sentence.Verb;
import com.rokuan.calliopecore.sentence.VerbConjugation;

/**
 * Created by LEBEAU Christophe on 07/03/2015.
 */
public class CalliopeVerbConjugation extends VerbConjugation {
    public static VerbConjugation buildFromCursor(Cursor result){
        CalliopeVerbConjugation conjugation = new CalliopeVerbConjugation();

        conjugation.name = result.getString(1);
        conjugation.form = Verb.Form.valueOf(result.getString(3));
        conjugation.tense = Verb.ConjugationTense.valueOf(result.getString(4));

        try {
            conjugation.pronoun = Verb.Pronoun.values()[result.getInt(5)];
        } catch (Exception e){
            conjugation.pronoun = Verb.Pronoun.UNDEFINED;
        }

        return conjugation;
    }
}

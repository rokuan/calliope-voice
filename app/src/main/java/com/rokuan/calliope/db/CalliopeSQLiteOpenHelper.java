package com.rokuan.calliope.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rokuan.calliope.constants.DataFile;
import com.rokuan.calliopecore.parser.Interpreter;
import com.rokuan.calliopecore.parser.SpeechParser;
import com.rokuan.calliopecore.parser.WordBuffer;
import com.rokuan.calliopecore.parser.WordDatabase;
import com.rokuan.calliopecore.sentence.Verb;
import com.rokuan.calliopecore.sentence.VerbConjugation;
import com.rokuan.calliopecore.sentence.Word;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.data.DateConverter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by LEBEAU Christophe on 19/02/2015.
 */
public class CalliopeSQLiteOpenHelper extends SQLiteOpenHelper implements WordDatabase, SpeechParser {
    private static final String DB_NAME = "calliope";
    private static final int DB_VERSION = 1;

    private static final int VERBS = 0;
    private static final int CONJUGATION = 1;
    private static final int WORDS = 2;
    private static final int COUNTRIES = 3;
    private static final int FIRSTNAMES = 4;

    private static final String[] TABLES = new String[]{
            "verbs",
            "conjugation",
            "words",
            "countries",
            "firstnames"
    };

    private static final String[] excludeNumericalPosition = new String[]{
            "antépénultième",
            "combientième",
            "énième",
            "nième",
            "pénultième",
            "quantième"
    };

    //public static final String VERB_ID = "verb_id";
    public static final String VERB_NAME = "verb_name";
    public static final String VERB_ACTION = "verb_action";
    //public static final String VERB_GERUND = "verb_gerund";
    public static final String VERB_AUXILIARY = "verb_auxiliary";

    public static final String CONJUGATION_ID = "conjugation_id";
    public static final String CONJUGATION_VERB = "conjugation_verb";
    public static final String CONJUGATION_VALUE = "conjugation_value";
    public static final String CONJUGATION_FORM = "conjugation_form";
    public static final String CONJUGATION_TENSE = "conjugation_tense";
    public static final String CONJUGATION_PERSON = "conjugation_person";

    public static final String WORD_NAME = "word_name";
    public static final String WORD_CATEGORY = "word_category";

    //public static final String COUNTRY_ID = "country_id";
    public static final String COUNTRY_CODE = "country_code";
    public static final String COUNTRY_CODE_EXTENDED = "country_code_extended";
    public static final String COUNTRY_NAME = "country_name";
    /*public static final String CITY_ID = "city_id";
    public static final String CITY_CODE = "city_code";
    public static final String CITY_CODE_EXTENDED = "city_code_extended";
    public static final String CITY_NAME = "city_name";*/
    /*public static final String CITY_FR_NAME = "city_name";
    public static final String CITY_EN_NAME = "city_en_name"*/

    public static final String FIRSTNAME_ID = "firstname_id";
    public static final String FIRSTNAME_VALUE = "firstname_value";
    public static final String FIRSTNAME_GENRE = "firstname_genre";
    // TODO: ajouter les autres colonnes

    private static final String VERB_QUERY = "CREATE TABLE " + TABLES[VERBS] + " (" +
            VERB_NAME + " TEXT PRIMARY KEY, " +
            VERB_ACTION + " TEXT NOT NULL, " +
            VERB_AUXILIARY + " INTEGER NOT NULL" +
            ")";
    private static final String CONJUGATION_QUERY = "CREATE TABLE " + TABLES[CONJUGATION] + " (" +
            CONJUGATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CONJUGATION_VERB + " TEXT NOT NULL, " +
            CONJUGATION_VALUE + " TEXT NOT NULL, " +
            CONJUGATION_FORM + " TEXT NOT NULL, " +
            CONJUGATION_TENSE + " TEXT NOT NULL, " +
            CONJUGATION_PERSON + " INTEGER NOT NULL, " +
            "FOREIGN KEY(" + CONJUGATION_VERB + ") REFERENCES " + TABLES[VERBS] + "(" + VERB_NAME + ")" +
            ")";
    private static final String WORD_QUERY = "CREATE TABLE " + TABLES[WORDS] + " (" +
            WORD_NAME + " TEXT PRIMARY KEY, " +
            WORD_CATEGORY + " TEXT NOT NULL" +
            ")";
    private static final String COUNTRY_QUERY = "CREATE TABLE " + TABLES[COUNTRIES] + " (" +
            COUNTRY_CODE + " VARCHAR(2) PRIMARY KEY, " +
            COUNTRY_CODE_EXTENDED + " VARCHAR(3) UNIQUE, " +
            COUNTRY_NAME + " TEXT NOT NULL" +
            ")";


    private Context context;
    //private DatabaseLoadingListener listener;

    public CalliopeSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    /*public void setDatabaseLoadingListener(DatabaseLoadingListener dbListener){
        listener = dbListener;
    }*/

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*if(listener != null){
            listener.setOperationsCount(TABLES.length);
            listener.onLoadingStarted();
        }*/

        db.execSQL(VERB_QUERY);
        db.execSQL(CONJUGATION_QUERY);
        db.execSQL(WORD_QUERY);

        //notifyOperationStarted(0, "Verbes");
        loadAllVerbs(db, "verbs.txt");
        //notifyOperationEnded(0);

        //notifyOperationStarted(1, "Conjugaison");
        loadAllConjugatedVerbs(db, "conjugation.txt");
        //notifyOperationEnded(1);

        //notifyOperationStarted(2, "Mots");
        loadAllWords(db, "words.txt");
        //notifyOperationEnded(2);

        //notifyOperationStarted(3, "Places");
        //notifyOperationEnded(3);

        /*if(listener != null) {
            listener.onLoadingEnded();
        }*/
    }

    /*private void notifyOperationStarted(int operationIndex, String message){
        if(listener != null){
            listener.onOperationStarted(operationIndex, message);
        }
    }

    private void notifyOperationEnded(int operationIndex){
        if(listener != null){
            listener.onOperationEnded(operationIndex);
        }
    }*/

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(String tableName: TABLES) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
        }

        this.onCreate(db);
    }

    private void loadAllVerbs(SQLiteDatabase db, String assetName){
        AssetManager assets = this.context.getAssets();
        InputStream is = null;
        Scanner sc = null;

        try {
            is = assets.open(assetName);
            sc = new Scanner(is, DataFile.DEFAULT_ENCODING);

            while(sc.hasNextLine()){
                String line = sc.nextLine();
                String[] fields = line.split(DataFile.SEPARATOR);
                String verb = fields[0];
                String action = fields[1];
                boolean isAuxiliary = (verb.equals("avoir") || verb.equals("être"));
                ContentValues values = new ContentValues();

                values.put(VERB_NAME, verb);
                values.put(VERB_ACTION, action);
                values.put(VERB_AUXILIARY, isAuxiliary);

                db.insert(TABLES[VERBS], null, values);
            }
        } catch (IOException e) {
            Log.e("CalliopeSQL", "(loadAllVerbs)" + e.getMessage());
        } finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {

                }
            }

            if(sc != null)
                sc.close();
        }
    }

    private void loadAllConjugatedVerbs(SQLiteDatabase db, String assetName){
        AssetManager assets = this.context.getAssets();
        InputStream is = null;
        Scanner sc = null;

        try {
            is = assets.open(assetName);
            sc = new Scanner(is, DataFile.DEFAULT_ENCODING);

            while(sc.hasNextLine()){
                String line = sc.nextLine();
                String[] fields = line.split(DataFile.SEPARATOR);
                String verb = fields[0];
                String value = fields[1];
                String form = fields[2];
                String tense = fields[3];
                int person = Integer.parseInt(fields[4]);
                ContentValues values = new ContentValues();

                values.put(CONJUGATION_VERB, verb);
                values.put(CONJUGATION_VALUE, value);
                values.put(CONJUGATION_FORM, form);
                values.put(CONJUGATION_TENSE, tense);
                values.put(CONJUGATION_PERSON, person);

                db.insert(TABLES[CONJUGATION], null, values);
            }
        } catch (IOException e) {
            Log.e("CalliopeSQL", "(loadAllConjugatedVerbs)" + e.getMessage());
        } finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {

                }
            }

            if(sc != null)
                sc.close();
        }
    }

    private void loadAllWords(SQLiteDatabase db, String assetName){
        AssetManager assets = this.context.getAssets();
        InputStream is = null;
        Scanner sc = null;

        try {
            is = assets.open(assetName);
            //sc = new Scanner(is, DataFile.DEFAULT_ENCODING);
            sc = new Scanner(is);

            while(sc.hasNextLine()){
                String line = sc.nextLine();
                String[] fields = line.split(DataFile.SEPARATOR);
                String word = fields[0];
                String categories = fields[1];
                ContentValues values = new ContentValues();

                values.put(WORD_NAME, word);
                values.put(WORD_CATEGORY, categories);

                db.insert(TABLES[WORDS], null, values);
            }
        } catch (IOException e) {
            Log.e("CalliopeSQL", "(loadAllWords)" + e.getMessage());
        } finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {

                }
            }

            if(sc != null)
                sc.close();
        }
    }

    private void loadAllCountries(SQLiteDatabase db, String assetName){
        AssetManager assets = this.context.getAssets();
        InputStream is = null;
        Scanner sc = null;

        try {
            is = assets.open(assetName);
            //sc = new Scanner(is, DataFile.DEFAULT_ENCODING);
            sc = new Scanner(is);

            while(sc.hasNextLine()){
                String line = sc.nextLine();
                String[] fields = line.split(DataFile.SEPARATOR);
                String code = fields[0];
                String extendedCode = fields[1];
                String name = fields[2];
                ContentValues values = new ContentValues();

                values.put(COUNTRY_CODE, code);
                values.put(COUNTRY_CODE_EXTENDED, extendedCode);
                values.put(COUNTRY_NAME, name);

                db.insert(TABLES[COUNTRIES], null, values);
            }
        } catch (IOException e) {
            Log.e("CalliopeSQL", "(loadAllCountries)" + e.getMessage());
        } finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {

                }
            }

            if(sc != null)
                sc.close();
        }
    }



    public Verb<?> getVerb(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results = db.query(TABLES[VERBS], null, VERB_NAME + " = ?", new String[]{ name }, null, null, null);
        Verb verb = null;

        if(results.getCount() > 0){
            results.moveToFirst();
            //Log.i("CalliopeSQL - getVerb", "found '" + name + "'");
            verb = CalliopeVerb.buildFromCursor(results);
        }

        results.close();
        db.close();
        return verb;
    }

    //public VerbConjugation findConjugatedVerb(SQLiteDatabase db, String conjugatedVerb){
    public VerbConjugation findConjugatedVerb(String conjugatedVerb){
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor matchingResults = db.query(TABLES[CONJUGATION], null, "? LIKE " + CONJUGATION_VALUE + "% ORDER BY length(" + CONJUGATION_VERB + ")", new String[]{ conjugatedVerb }, null, null, null);
        // TODO: modifier la requete pour qu'elle matche les accords en genre et en nombre
        //Cursor matchingResults = db.query(TABLES[CONJUGATION], null, CONJUGATION_VALUE + " LIKE ? || '%'", new String[]{ conjugatedVerb }, null, null, null);
        Cursor matchingResults = db.query(TABLES[CONJUGATION], null, CONJUGATION_VALUE + " = ?", new String[]{ conjugatedVerb }, null, null, null);

        if(matchingResults.moveToFirst()){
            VerbConjugation result = CalliopeVerbConjugation.buildFromCursor(matchingResults);
            String verbName = matchingResults.getString(1);

            matchingResults.close();
            db.close();

            result.setVerb(getVerb(verbName));
            return result;
        }

        db.close();
        matchingResults.close();
        return null;
    }


    public Word findWord(String w){
        Word result = null;

        Cursor selection;

        if(w.matches(DateConverter.fullTimeRegex) || w.matches(DateConverter.hourOnlyRegex)){
            return new Word(w, Word.WordType.TIME);
        }

        /*if(Character.isDigit(w.charAt(0))){
            return new Word(Word.WordType.NUMBER, w);
        }*/
        if(Character.isDigit(w.charAt(0))) {
            try {
                return new Word(w, Word.WordType.NUMBER);
            } catch (Exception e) {
                Matcher matcher = Pattern.compile("[0-9]+e").matcher(w);

                if (matcher.find()) {
                    String matchingValue = matcher.group(0);
                    long longValue = Long.parseLong(matchingValue.substring(0, matchingValue.length() - 1));
                    return new Word(String.valueOf(longValue), Word.WordType.NUMERICAL_POSITION);
                }
            }
        }

        if(Character.isUpperCase(w.charAt(0))){
            return new Word(w, Word.WordType.PROPER_NAME);
        }

        List<Word.WordType> wordTypes = new ArrayList<>();

        if(w.endsWith("ième")){
            boolean isNumericalPosition = true;

            for(String exclude: excludeNumericalPosition){
                if(w.startsWith(exclude)){
                    isNumericalPosition = false;
                    break;
                }
            }

            if(isNumericalPosition) {
                wordTypes.add(Word.WordType.NUMERICAL_POSITION);
            }
        }

        VerbConjugation conjugation = findConjugatedVerb(w);

        if(conjugation != null){
            wordTypes.add(Word.WordType.VERB);

            if(conjugation.getVerb() != null && conjugation.getVerb().isAuxiliary()){
                wordTypes.add(Word.WordType.AUXILIARY);
            }
        }

        SQLiteDatabase db = this.getReadableDatabase();
        //selection.close();
        selection = db.query(TABLES[WORDS], null, WORD_NAME + " = ?", new String[]{ w }, null, null, null);

        if(selection.getCount() > 0){
            selection.moveToFirst();
            Word tmpResult = CalliopeWord.buildFromCursor(selection);
            wordTypes.addAll(tmpResult.getTypes());
        }

        if(wordTypes.size() > 0) {
            result = new CalliopeWord(w, wordTypes);
            result.setVerbInfo(conjugation);
        }

        selection.close();
        db.close();
        return result;
    }

    @Override
    public WordBuffer lexSpeech(String speech) {
        WordBuffer words = new WordBuffer();
        String[] parts = speech.split(" ");

        for(int i=0; i<parts.length; i++){
            String elem = parts[i];
            Word tmp = findWord(elem);

            if(tmp == null){
                // TODO: verifier que le cas "d'aujourd'hui" est bien pris en charge
                int firstQuoteIndex = elem.indexOf(DataFile.WORD_SEPARATOR_QUOTE);

                if(firstQuoteIndex != -1) {
                    String leftPart = elem.substring(0, firstQuoteIndex);
                    String rightPart = elem.substring(firstQuoteIndex + 1, elem.length());

                    tmp = findWord(leftPart);

                    if(tmp != null){
                        words.add(tmp);
                    }

                    tmp = findWord(rightPart);

                    if(tmp != null){
                        words.add(tmp);
                    }
                } else {
                    String[] subParts = elem.split(DataFile.WORD_SEPARATOR_DASH);

                    for(String internalElem: subParts){
                        tmp = findWord(internalElem);

                        if(tmp != null){
                            words.add(tmp);
                        }
                    }
                }
            } else {
                words.add(tmp);
            }
        }

        return words;
    }

    public InterpretationObject parseSpeech(String speech) {
        WordBuffer words = lexSpeech(speech.trim());
        System.out.println(words);
        return parseSpeech(words);
    }

    @Override
    public InterpretationObject parseSpeech(WordBuffer words) {
        Interpreter interpreter = new Interpreter();
        return interpreter.parseInterpretationObject(words);
    }
}

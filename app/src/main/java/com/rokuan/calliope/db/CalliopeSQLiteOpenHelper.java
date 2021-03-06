package com.rokuan.calliope.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rokuan.calliope.R;
import com.rokuan.calliope.constants.DataFile;
import com.rokuan.calliopecore.parser.Parser;
import com.rokuan.calliopecore.parser.SpeechParser;
import com.rokuan.calliopecore.parser.WordBuffer;
import com.rokuan.calliopecore.parser.WordDatabase;
import com.rokuan.calliopecore.sentence.CityInfo;
import com.rokuan.calliopecore.sentence.CountryInfo;
import com.rokuan.calliopecore.sentence.LanguageInfo;
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

import de.greenrobot.event.EventBus;


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
    private static final int CITIES = 4;
    private static final int LANGUAGES = 5;
    private static final int FIRSTNAMES = 6;

    private static final String[] TABLES = new String[]{
            "verbs",
            "conjugation",
            "words",
            "countries",
            "cities",
            "languages",
            "firstnames"
    };

    private static final String[] NOT_A_NUMERICAL_POSITION = new String[]{
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

    public static final String COUNTRY_CODE = "country_code";
    public static final String COUNTRY_CODE_EXTENDED = "country_code_extended";
    public static final String COUNTRY_NAME = "country_name";

    public static final String CITY_ID = "city_id";
    public static final String CITY_NAME = "city_name";
    public static final String CITY_LATITUDE = "city_latitude";
    public static final String CITY_LONGITUDE = "city_longitude";

    public static final String LANGUAGE_CODE = "language_code";
    public static final String LANGUAGE_NAME = "language_name";

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
    private static final String CITY_QUERY = "CREATE TABLE " + TABLES[CITIES] + " (" +
            CITY_ID + " INTEGER PRIMARY KEY, " +
            CITY_NAME + " TEXT NOT NULL, " +
            CITY_LATITUDE + " NUMERIC NOT NULL, " +
            CITY_LONGITUDE + " NUMERIC NOT NULL" +
            ")";
    private static final String LANGUAGE_QUERY = "CREATE TABLE " + TABLES[LANGUAGES] + " (" +
            LANGUAGE_NAME + " TEXT PRIMARY KEY, " +
            LANGUAGE_CODE + " VARCHAR(2) NOT NULL" +
            ")";


    private Context context;

    public CalliopeSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(VERB_QUERY);
        db.execSQL(CONJUGATION_QUERY);
        db.execSQL(WORD_QUERY);
        db.execSQL(LANGUAGE_QUERY);
        db.execSQL(COUNTRY_QUERY);
        db.execSQL(CITY_QUERY);

        EventBus bus = EventBus.getDefault();

        bus.post(new TableEvent(context.getString(R.string.db_verbs)));
        loadAllVerbs(db, "verbs.txt");
        bus.post(new TableEvent(context.getString(R.string.db_conjugations)));
        loadAllConjugatedVerbs(db, "conjugation.txt");
        bus.post(new TableEvent(context.getString(R.string.db_words)));
        loadAllWords(db, "words.txt");
        bus.post(new TableEvent(context.getString(R.string.db_languages)));
        loadAllLanguages(db, "languages.txt");
        bus.post(new TableEvent(context.getString(R.string.db_countries)));
        loadAllCountries(db, "countries.txt");
        bus.post(new TableEvent(context.getString(R.string.db_cities)));
        loadAllCities(db, "cities.txt");
    }

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
                /*String code = fields[0];
                String extendedCode = fields[1];
                String name = fields[2];*/
                String code = fields[2];
                String extendedCode = fields[3];
                String name = fields[4];
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

    private void loadAllCities(SQLiteDatabase db, String assetName){
        AssetManager assets = this.context.getAssets();
        InputStream is = null;
        Scanner sc = null;

        try {
            is = assets.open(assetName);
            sc = new Scanner(is);

            while(sc.hasNextLine()){
                String line = sc.nextLine();
                String[] fields = line.split(DataFile.SEPARATOR);
                double latitude = Double.parseDouble(fields[0]);
                double longitude = Double.parseDouble(fields[1]);
                String name = fields[2];
                ContentValues values = new ContentValues();

                values.put(CITY_NAME, name);
                values.put(CITY_LATITUDE, latitude);
                values.put(CITY_LONGITUDE, longitude);

                db.insert(TABLES[CITIES], null, values);
            }
        } catch (IOException e) {
            Log.e("CalliopeSQL", "(loadAllCities)" + e.getMessage());
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

    private void loadAllLanguages(SQLiteDatabase db, String assetName){
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
                String name = fields[0];
                String code = fields[1];
                ContentValues values = new ContentValues();

                values.put(LANGUAGE_NAME, name);
                values.put(LANGUAGE_CODE, code);

                db.insert(TABLES[LANGUAGES], null, values);
            }
        } catch (IOException e) {
            Log.e("CalliopeSQL", "(loadAllLanguages)" + e.getMessage());
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

    public boolean isAFirstName(String word){
        // TODO:
        return false;
    }

    public LanguageInfo findLanguage(String lang){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor matchingResults = db.query(TABLES[LANGUAGES], null, LANGUAGE_NAME + " = ?", new String[]{ lang }, null, null, null);
        LanguageInfo result = null;

        if(matchingResults.moveToFirst()){
            result = new LanguageInfo(matchingResults.getString(0), matchingResults.getString(1));
        }

        db.close();
        matchingResults.close();
        return result;
    }

    public CountryInfo findCountry(String country){
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor matchingResults = db.query(TABLES[COUNTRIES], null, "INSTR(" + COUNTRY_NAME + ", ?)", new String[]{ country }, null, null, null);
        Cursor matchingResults = db.query(TABLES[COUNTRIES], null, COUNTRY_NAME + " LIKE '" + country + "%'", null, null, null, null);
        CountryInfo result = null;

        if(matchingResults.moveToFirst()){
            result = new CountryInfo(matchingResults.getString(0), matchingResults.getString(2));
        }

        db.close();
        matchingResults.close();
        return result;
    }

    public CityInfo findCity(String city){
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor matchingResults = db.query(TABLES[CITIES], null, "INSTR(" + CITY_NAME + ", ?)", new String[]{ city }, null, null, null);
        Cursor matchingResults = db.query(TABLES[CITIES], null, CITY_NAME + " LIKE '%" + city + "%'", null, null, null, null);
        CityInfo result = null;

        if(matchingResults.moveToFirst()){
            result = new CityInfo(matchingResults.getString(1), matchingResults.getDouble(2), matchingResults.getDouble(3));
            Log.i("Found city", result.getName() + "(" + result.getLatitude() +", " + result.getLongitude() + ")");
        }

        db.close();
        matchingResults.close();
        return result;
    }

    public Word findWord(String w){
        Word result = null;
        Cursor selection;

        if(w.matches(DateConverter.FULL_TIME_REGEX) || w.matches(DateConverter.HOUR_ONLY_REGEX)){
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

        CountryInfo country = null;
        CityInfo city = null;
        VerbConjugation conjugation = null;
        LanguageInfo language = null;
        List<Word.WordType> wordTypes = new ArrayList<>();

        if(Character.isUpperCase(w.charAt(0))){
            if(isAFirstName(w)){
                wordTypes.add(Word.WordType.FIRSTNAME);
            }

            // TOCHECK: privilegier les noms de villes aux noms des pays ?
            city = findCity(w);
            country = findCountry(w);

            if(city != null){
                wordTypes.add(Word.WordType.CITY);
            } else if(country != null) {
                wordTypes.add(Word.WordType.COUNTRY);
            } else {
                wordTypes.add(Word.WordType.PROPER_NAME);
            }

            //return new Word(w, wordTypes);
        } else {
            if(w.endsWith("ième")){
                boolean isNumericalPosition = true;

                for(String exclude: NOT_A_NUMERICAL_POSITION){
                    if(w.startsWith(exclude)){
                        isNumericalPosition = false;
                        break;
                    }
                }

                if(isNumericalPosition) {
                    wordTypes.add(Word.WordType.NUMERICAL_POSITION);
                }
            }

            conjugation = findConjugatedVerb(w);

            if(conjugation != null){
                wordTypes.add(Word.WordType.VERB);

                if(conjugation.getVerb() != null && conjugation.getVerb().isAuxiliary()){
                    wordTypes.add(Word.WordType.AUXILIARY);
                }
            }

            language = findLanguage(w);

            if(language != null){
                wordTypes.add(Word.WordType.LANGUAGE);
            }

            SQLiteDatabase db = this.getReadableDatabase();
            selection = db.query(TABLES[WORDS], null, WORD_NAME + " = ?", new String[]{ w }, null, null, null);

            if(selection.moveToFirst()){
                selection.moveToFirst();
                Word tmpResult = CalliopeWord.buildFromCursor(selection);
                wordTypes.addAll(tmpResult.getTypes());
            }

            selection.close();
            db.close();
        }

        if(wordTypes.size() > 0) {
            result = new CalliopeWord(w, wordTypes);
            result.setVerbInfo(conjugation);
            result.setLanguageInfo(language);
            result.setCountryInfo(country);
            result.setCityInfo(city);
        }

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
        Parser interpreter = new Parser();
        return interpreter.parseInterpretationObject(words);
    }
}

package com.rokuan.calliope.api.microsoft;

/**
 * Created by LEBEAU Christophe on 22/05/15.
 */
public class TranslationData {
    private String sourceLanguage;
    private String targetLanguage;
    private String originalText;
    private String translatedText;

    public TranslationData(String fromLang, String toLang, String originalTxt, String translatedTxt){
        sourceLanguage = fromLang;
        targetLanguage = toLang;
        originalText = originalTxt;
        translatedText = translatedTxt;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public String getOriginalText() {
        return originalText;
    }

    public String getTranslatedText() {
        return translatedText;
    }
}

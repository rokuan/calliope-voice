package com.rokuan.calliope.modules;

import com.rokuan.calliope.HomeActivity;
import com.rokuan.calliope.source.SourceObject;
import com.rokuan.calliope.source.TextSource;
import com.rokuan.calliopecore.sentence.Action;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.nominal.LanguageObject;
import com.rokuan.calliopecore.sentence.structure.nominal.NominalGroup;

import java.util.Locale;

/**
 * Created by Lebeau Lucie on 18/05/15.
 */
public class LanguageModule extends CalliopeModule {
    public LanguageModule(HomeActivity a) {
        super(a);
    }

    @Override
    public boolean canHandle(InterpretationObject object) {
        if(object.getType() == InterpretationObject.RequestType.ORDER){
            switch((Action.VerbAction)object.action){
                case TRANSLATE:
                    // TODO: verifier
                    return true;
            }
        }

        return false;
    }

    @Override
    public boolean submit(InterpretationObject object) {
        if(object.getType() == InterpretationObject.RequestType.ORDER){
            switch((Action.VerbAction)object.action){
                case TRANSLATE:
                    SourceObject.ObjectType defaultType = SourceObject.ObjectType.TEXT;
                    String langCode = Locale.getDefault().getLanguage();

                    if(object.how != null && object.how.getType() == NominalGroup.GroupType.LANGUAGE){
                        langCode = ((LanguageObject)object.how).language.getCode();
                    }

                    TextSource txtSource = (TextSource)this.getActivity().getLastSourceOfType(defaultType);

                    if(txtSource != null) {
                        System.out.println("Traduction du texte \"" + txtSource.getText() +"\" en \"" + langCode + "\"");
                        return true;
                    }
            }
        }
        return false;
    }
}

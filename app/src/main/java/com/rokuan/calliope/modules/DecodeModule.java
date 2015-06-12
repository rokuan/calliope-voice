package com.rokuan.calliope.modules;

import com.rokuan.calliope.HomeActivity;
import com.rokuan.calliopecore.sentence.Action;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.nominal.ComplementObject;
import com.rokuan.calliopecore.sentence.structure.nominal.NominalGroup;

/**
 * Created by LEBEAU Christophe on 18/05/15.
 */
public class DecodeModule extends CalliopeModule {
    public DecodeModule(HomeActivity a) {
        super(a);
    }

    @Override
    public boolean canHandle(InterpretationObject object) {
        switch((Action.VerbAction)object.action){
            // TODO: remplacer par les bonnes actions
            case GET:
            default:
                if(object.what != null && object.what.getType() == NominalGroup.GroupType.COMPLEMENT){
                    ComplementObject compl = (ComplementObject)object.what;
                }

                //if(object.where != null && object.where.getType() == )
                break;
        }
        return false;
    }

    @Override
    public boolean submit(InterpretationObject object) {
        return false;
    }
}

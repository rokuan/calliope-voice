package com.rokuan.calliope.source;

import com.rokuan.calliope.receiver.SmsData;

/**
 * Created by LEBEAU Christophe on 25/05/15.
 */
public class SmsSource extends SourceObject {
    private SmsData data;

    public SmsSource(SmsData d) {
        super(ObjectType.SMS);
        data = d;
    }

    public SmsData getData(){
        return data;
    }
}

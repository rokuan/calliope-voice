package com.rokuan.calliope.source;

import java.util.Date;

/**
 * Created by LEBEAU Christophe on 29/04/2015.
 */
public class AlarmSource extends SourceObject {
    private Date time;

    public AlarmSource(Date t) {
        super(ObjectType.ALARM);
        time = t;
    }

    public Date getTime(){
        return time;
    }
}

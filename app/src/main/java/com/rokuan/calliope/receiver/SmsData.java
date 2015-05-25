package com.rokuan.calliope.receiver;

import java.util.List;

/**
 * Created by LEBEAU Christophe on 25/05/15.
 */
public class SmsData {
    // TODO: create a User object (nullable username + phone number) ?
    private String from;
    private List<String> to;
    private String content;

    public SmsData(String f, List<String> t, String c){
        from = f;
        to = t;
        content = c;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return from;
    }

    public List<String> getReceiver() {
        return to;
    }
}

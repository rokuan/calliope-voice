package com.rokuan.calliope.api.wikipedia;

/**
 * Created by LEBEAU Christophe on 20/04/2015.
 */
public class Person {
    private String name;
    private String content;

    public Person(String personName, String personDescription){
        name = personName;
        content = personDescription;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}

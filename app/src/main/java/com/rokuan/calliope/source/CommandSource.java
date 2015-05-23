package com.rokuan.calliope.source;

/**
 * Created by LEBEAU Christophe on 21/05/15.
 */
public class CommandSource extends SourceObject {
    private String command;

    public CommandSource(String cmd) {
        super(ObjectType.COMMAND);
        command = cmd;
    }

    public String getCommand() {
        return command;
    }
}

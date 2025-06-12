package com.klanting.signclick.migrations;

public abstract class Migration {

    private final String from;

    private final String to;

    Migration(String from, String to){
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public abstract void migrate();
}

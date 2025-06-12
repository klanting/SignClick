package com.klanting.signclick.economy.logs;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

abstract public class PluginLogs {
    /*
    * A logging system, to log information later accessible in minecraft by users
    * Logs remain for a configurable duration.
    *
    * This class follows the observer design pattern
    * */
    private final String title;

    public PluginLogs(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public abstract void update(String action, String message, UUID issuer);
    public abstract List<ImmutablePair<LocalDateTime, String>> getLogs();



}

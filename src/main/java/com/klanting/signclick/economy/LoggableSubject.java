package com.klanting.signclick.economy;

import com.klanting.signclick.economy.logs.PluginLogs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

abstract class LoggableSubject {
    private final List<PluginLogs> logObservers = new ArrayList<>();

    public void addObserver(PluginLogs observer){
        logObservers.add(observer);
    }

    public void update(String action, String message, UUID issuer){
        for (PluginLogs pluginLog: logObservers){
            pluginLog.update(action, message, issuer);
        }
    }

    public List<PluginLogs> getLogObservers(){
        return logObservers;
    }
}

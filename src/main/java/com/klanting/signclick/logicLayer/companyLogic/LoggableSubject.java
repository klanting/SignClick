package com.klanting.signclick.logicLayer.companyLogic;

import com.klanting.signclick.logicLayer.companyLogic.logs.PluginLogs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

abstract class LoggableSubject {
    protected List<PluginLogs> logObservers = new ArrayList<>();

    public void addObserver(PluginLogs observer){
        logObservers.add(observer);
    }

    public void update(String action, Object message, UUID issuer){
        for (PluginLogs pluginLog: logObservers){
            pluginLog.update(action, message, issuer);
        }
    }

    public List<PluginLogs> getLogObservers(){
        return logObservers;
    }
}

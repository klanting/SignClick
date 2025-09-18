package com.klanting.signclick.logicLayer.logs;

import org.apache.commons.lang3.tuple.MutableTriple;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ContractChange extends PluginLogs {

    public final List<MutableTriple<LocalDateTime, String, String>> contractUpdates = new ArrayList<>();

    public ContractChange(){
        super("Contract Change Logs");
    }

    @Override
    public void update(String action, Object message, UUID issuer) {
        if (!action.equals("Contract Signed")){
            return;
        }

        if(!(message instanceof String mess)){
            return;
        }

        Instant now = Instant.now();
        LocalDateTime ldt = LocalDateTime.ofInstant(now, ZoneId.systemDefault());

        contractUpdates.add(MutableTriple.of(ldt, action, mess));
    }

    @Override
    public List<MutableTriple<LocalDateTime, String, String>> getLogs() {
        return contractUpdates;
    }

}

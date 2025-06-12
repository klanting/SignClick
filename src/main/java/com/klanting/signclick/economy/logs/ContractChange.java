package com.klanting.signclick.economy.logs;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ContractChange extends PluginLogs {

    public final List<ImmutablePair<LocalDateTime, String>> contractUpdates = new ArrayList<>();

    public ContractChange(){
        super("Contract Change Logs");
    }

    @Override
    public void update(String action, String message, UUID issuer) {
        if (!action.equals("contractChange")){
            return;
        }

        Instant now = Instant.now();
        LocalDateTime ldt = LocalDateTime.ofInstant(now, ZoneId.systemDefault());

        contractUpdates.add(ImmutablePair.of(ldt, message));
    }

    @Override
    public List<ImmutablePair<LocalDateTime, String>> getLogs() {
        return contractUpdates;
    }

}

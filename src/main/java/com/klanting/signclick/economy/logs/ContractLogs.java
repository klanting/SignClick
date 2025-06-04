package com.klanting.signclick.economy.logs;

import org.apache.commons.lang3.tuple.Pair;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ContractLogs extends PluginLogs {

    private final List<Pair<LocalDateTime, String>> contractUpdates = new ArrayList<>();

    @Override
    public void update(String action, String message, UUID issuer) {
        if (!action.equals("contractPayment")){
            return;
        }

        Instant now = Instant.now();
        LocalDateTime ldt = LocalDateTime.ofInstant(now, ZoneId.systemDefault());

        contractUpdates.add(Pair.of(ldt, message));
    }

    @Override
    public List<Pair<LocalDateTime, String>> getLogs() {
        return contractUpdates;
    }

}

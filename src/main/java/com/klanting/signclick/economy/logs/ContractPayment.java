package com.klanting.signclick.economy.logs;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ContractPayment extends PluginLogs {

    public final List<MutableTriple<LocalDateTime, String, String>> contractUpdates = new ArrayList<>();

    public ContractPayment(){
        super("Contract Payment Logs");
    }

    @Override
    public void update(String action, String message, UUID issuer) {
        if (!action.equals("Contract Payment")){
            return;
        }

        Instant now = Instant.now();
        LocalDateTime ldt = LocalDateTime.ofInstant(now, ZoneId.systemDefault());

        contractUpdates.add(MutableTriple.of(ldt, action, message));
    }

    @Override
    public List<MutableTriple<LocalDateTime, String, String>> getLogs() {
        return contractUpdates;
    }

}

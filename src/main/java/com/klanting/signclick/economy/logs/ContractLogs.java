package com.klanting.signclick.economy.logs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ContractLogs extends PluginLogs {

    private final List<String> contractUpdates = new ArrayList<>();

    public ContractLogs(){

    }

    @Override
    void update(String action, String message, UUID issuer) {
        if (!action.equals("contractUpdate")){
            return;
        }

        contractUpdates.add("Dummy Message");
    }
}

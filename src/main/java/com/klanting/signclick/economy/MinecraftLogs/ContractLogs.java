package com.klanting.signclick.economy.MinecraftLogs;

import java.util.UUID;

public class ContractLogs extends MinecraftLogs{

    public ContractLogs(){

    }

    @Override
    void update(String action, String message, UUID issuer) {
        if (!action.equals("logContract")){
            return;
        }
    }
}

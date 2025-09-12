package com.klanting.signclick.economy.logs;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MoneyTransfer extends PluginLogs {

    public final List<MutableTriple<LocalDateTime, String, String>> MoneyTransfers = new ArrayList<>();

    public MoneyTransfer(){
        super("Money Transfer Logs");
    }
    @Override
    public void update(String action, Object message, UUID issuer) {
        if (!action.equals("Balance added") && !action.equals("Balance removed")){
            return;
        }

        if(!(message instanceof String mess)){
            return;
        }

        Instant now = Instant.now();
        LocalDateTime ldt = LocalDateTime.ofInstant(now, ZoneId.systemDefault());

        String title = action;
        title += " by " + Bukkit.getOfflinePlayer(issuer).getName();

        MoneyTransfers.add(MutableTriple.of(
                ldt,
                title,
                mess));
    }

    @Override
    public List<MutableTriple<LocalDateTime, String, String>> getLogs() {
        return MoneyTransfers;
    }
}

package com.klanting.signclick.economy.logs;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShopLogs extends PluginLogs{

    public final List<MutableTriple<LocalDateTime, String, String>> ShopLogsEntries = new ArrayList<>();

    public ShopLogs(){
        super("Shop logs");
    }
    @Override
    public void update(String action, String message, UUID issuer) {
        if (!action.equals("Shop sales")){
            return;
        }

        Instant now = Instant.now();
        LocalDateTime ldt = LocalDateTime.ofInstant(now, ZoneId.systemDefault());

        ShopLogsEntries.add(MutableTriple.of(
                ldt,
                action+ (issuer != null ? (" by " + Bukkit.getOfflinePlayer(issuer).getName()) : ""),
                message));
    }

    @Override
    public List<MutableTriple<LocalDateTime, String, String>> getLogs() {
        return ShopLogsEntries;
    }
}

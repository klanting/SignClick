package com.klanting.signclick.economy.logs;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResearchUpdate extends PluginLogs{

    public final List<MutableTriple<LocalDateTime, String, String>> ShareholderChanges = new ArrayList<>();

    public ResearchUpdate(){
        super("Research Updates");
    }
    @Override
    public void update(String action, Object message, UUID issuer) {
        if (!action.equals("Research priority Change") && !action.equals("Research Completed")){
            return;
        }

        if(!(message instanceof String mess)){
            return;
        }

        Instant now = Instant.now();
        LocalDateTime ldt = LocalDateTime.ofInstant(now, ZoneId.systemDefault());

        ShareholderChanges.add(MutableTriple.of(
                ldt,
                action+ (issuer != null ? (" by " + Bukkit.getOfflinePlayer(issuer).getName()) : ""),
                mess));
    }

    @Override
    public List<MutableTriple<LocalDateTime, String, String>> getLogs() {
        return ShareholderChanges;
    }
}

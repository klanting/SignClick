package com.klanting.signclick.logicLayer.logs;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShareholderChange extends PluginLogs{
    public final List<MutableTriple<LocalDateTime, String, String>> ShareholderChanges = new ArrayList<>();

    public ShareholderChange(){
        super("Shareholder Changes");
    }
    @Override
    public void update(String action, Object message, UUID issuer) {
        if (!action.equals("Shares bought") && !action.equals("Shares sold") && !action.equals("Shares transferred")){
            return;
        }

        if(!(message instanceof String mess)){
            return;
        }

        Instant now = Instant.now();
        LocalDateTime ldt = LocalDateTime.ofInstant(now, ZoneId.systemDefault());

        ShareholderChanges.add(MutableTriple.of(
                ldt,
                action+" by " + Bukkit.getOfflinePlayer(issuer).getName(),
                mess));
    }

    @Override
    public List<MutableTriple<LocalDateTime, String, String>> getLogs() {
        return ShareholderChanges;
    }
}

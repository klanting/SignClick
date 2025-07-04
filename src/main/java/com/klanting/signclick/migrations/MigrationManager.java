package com.klanting.signclick.migrations;

import com.klanting.signclick.SignClick;
import org.bukkit.ChatColor;
import versionCompatibility.VersionDetection;

import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class MigrationManager {

    private static final List<Migration> migrations = List.of(
            new Migrationv100v101(),
            new Migrationv101v102(),
            new Migrationv102v200()
    );

    public static void Migrate(){
        String version = VersionDetection.getInstance().getVersion();
        String currentVersion = SignClick.getPlugin().getConfig().getString("version");

        if (currentVersion == null){
            currentVersion = "1.0.0";
        }
        if (currentVersion.equals(version)) {
            return;
        }

        for (Migration migration: migrations){

            if (!migration.getFrom().equals(currentVersion)){
                continue;
            }

            migration.migrate();
            currentVersion = migration.getTo();
        }

    }
}

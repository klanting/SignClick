package com.klanting.signclick.migrations;

import com.klanting.signclick.SignClick;
import versionCompatibility.VersionDetection;

import java.util.List;

public class MigrationManager {

    private static final List<Migration> migrations = List.of(
            new Migrationv100v101()
    );

    public static void Migrate(){
        String version = VersionDetection.getInstance().getVersion();
        String currentVersion = SignClick.getPlugin().getConfig().getString("version", "v1.0.0");

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

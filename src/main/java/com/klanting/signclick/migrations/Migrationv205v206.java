package com.klanting.signclick.migrations;

import com.klanting.signclick.SignClick;

public class Migrationv205v206 extends Migration{
    Migrationv205v206(){
        super("2.0.5", "2.0.6");
    }

    @Override
    public void migrate() {

        long autoSaveInterval = SignClick.getConfigManager().getConfig("general.yml").getLong("autoSaveInterval");

        SignClick.getConfigManager().getConfig("general.yml").set("autoSaveInterval", autoSaveInterval,
                "After how much seconds the server auto-saves its plugin data (only relevant for JSON storage)");

        SignClick.getConfigManager().getConfig("general.yml").options().copyDefaults(true);
        SignClick.getConfigManager().save();
    }
}

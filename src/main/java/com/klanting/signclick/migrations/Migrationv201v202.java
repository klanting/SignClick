package com.klanting.signclick.migrations;

import com.klanting.signclick.SignClick;

public class Migrationv201v202 extends Migration{
    Migrationv201v202(){
        super("2.0.1", "2.0.2");
    }

    @Override
    public void migrate() {

        SignClick.getConfigManager().getConfig("general.yml").set("version", "2.0.2",
                "Latest updated version, don't change this, it will be done automatically");

        SignClick.getConfigManager().getConfig("general.yml").options().copyDefaults(true);
        SignClick.getConfigManager().save();
    }
}

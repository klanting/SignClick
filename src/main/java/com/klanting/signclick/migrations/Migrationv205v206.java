package com.klanting.signclick.migrations;

import com.klanting.signclick.SignClick;

public class Migrationv205v206 extends Migration{
    Migrationv205v206(){
        super("2.0.5", "2.0.6");
    }

    @Override
    public void migrate() {

        SignClick.getConfigManager().getConfig("general.yml").set("version", "2.0.6",
                "Latest updated version, don't change this, it will be done automatically");

        SignClick.getConfigManager().getConfig("general.yml").options().copyDefaults(true);
        SignClick.getConfigManager().save();
    }
}

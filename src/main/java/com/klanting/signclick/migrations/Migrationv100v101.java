package com.klanting.signclick.migrations;

import com.klanting.signclick.SignClick;

public class Migrationv100v101 extends Migration{

    Migrationv100v101(){
        super("v1.0.0", "v1.0.1");
    }

    @Override
    public void migrate() {
        SignClick.getPlugin().getConfig().set("version", "v1.0.1");
    }
}

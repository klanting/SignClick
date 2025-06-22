package com.klanting.signclick.migrations;

import com.klanting.signclick.SignClick;

public class Migrationv101v102 extends Migration{
    Migrationv101v102(){
        super("1.0.1", "1.0.2");
    }

    @Override
    public void migrate() {
        SignClick.getPlugin().getConfig().set("version", "1.0.2");
    }
}

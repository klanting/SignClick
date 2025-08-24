package com.klanting.signclick.migrations;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.CompanyRef;
import com.klanting.signclick.economy.logs.PluginLogs;
import com.klanting.signclick.economy.logs.ResearchUpdate;
import com.klanting.signclick.utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

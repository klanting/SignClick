package com.klanting.signclick.migrations;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.logs.MachineProduction;
import com.klanting.signclick.logicLayer.companyLogic.logs.PluginLogs;
import com.klanting.signclick.logicLayer.companyLogic.logs.ShopLogs;
import com.klanting.signclick.utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Migrationv203v204 extends Migration{
    Migrationv203v204(){
        super("2.0.3", "2.0.4");
    }

    @Override
    public void migrate() {

        SignClick.getConfigManager().getConfig("general.yml").set("version", "2.0.4",
                "Latest updated version, don't change this, it will be done automatically");

        SignClick.getConfigManager().getConfig("general.yml").options().copyDefaults(true);
        SignClick.getConfigManager().save();
    }
}

package com.klanting.signclick.migrations;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.Board;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.CompanyOwnerManager;
import com.klanting.signclick.economy.Research;
import com.klanting.signclick.economy.companyUpgrades.*;
import com.klanting.signclick.utils.Utils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Migrationv200betav200 extends Migration{
    Migrationv200betav200(){
        super("2.0.0-beta", "2.0.0");
    }

    @Override
    public void migrate() {

        SignClick.getPlugin().getConfig().set("version", "2.0.0");

        SignClick.getPlugin().getConfig().options().copyDefaults(true);
        SignClick.getPlugin().saveConfig();
    }
}

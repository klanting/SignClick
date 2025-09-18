package com.klanting.signclick.migrations;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.configs.CommentConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class Migrationv200betav200 extends Migration{
    Migrationv200betav200(){
        super("2.0.0-beta", "2.0.0");
    }

    @Override
    public void migrate() {

        /*
        * split the configuration into multiple files
        * */
        Set<String> topLevelKeys = SignClick.getPlugin().getConfig().getKeys(false);
        for (String key: topLevelKeys){
            for (String newFile: List.of("general.yml", "countries.yml", "companies.yml")){
                CommentConfig cc = SignClick.getConfigManager().getConfig(newFile);
                if (cc.getKeys(false).contains(key)){
                    cc.set(key, SignClick.getPlugin().getConfig().get(key));
                }
            }
        }

        Path dir = Paths.get(SignClick.getPlugin().getDataFolder().getAbsolutePath());
        try{
            Files.delete(dir.resolve("config.yml"));
        }catch (IOException ignored){

        }

        SignClick.getConfigManager().getConfig("general.yml").set("version", "2.0.0",
                "Latest updated version, don't change this, it will be done automatically");

        SignClick.getConfigManager().getConfig("general.yml").options().copyDefaults(true);
        SignClick.getConfigManager().save();
    }
}

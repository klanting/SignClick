package com.klanting.signclick.storageLayer;

import com.klanting.signclick.storageLayer.entities.Entity;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public class EbeanProvider implements StorageProvider{
    private Database database;

    public void init(JavaPlugin plugin) {

        DataSourceConfig ds = new DataSourceConfig();
        //TODO make dynamic later
        ds.setDriver("org.sqlite.JDBC");
        ds.setUrl("jdbc:sqlite:" + new File(plugin.getDataFolder(), "data.db"));
        ds.setUsername("");
        ds.setPassword("");

        DatabaseConfig config = new DatabaseConfig();
        config.setName("default");
        config.setDataSourceConfig(ds);

        // 🔥 Tell Ebean where entities are
        config.addPackage("com.yourplugin.model");

        // Auto DDL (for development)
        config.setDdlGenerate(true);
        config.setDdlRun(true);
        config.setDdlExtra(false);

        database = DatabaseFactory.create(config);
    }

    public Database db() {
        return database;
    }

    public void shutdown() {
        if (database != null) {
            database.shutdown();
        }
    }

    @Override
    public boolean store(Entity entity) {
        return false;
    }
}

package com.klanting.signclick.storageLayer;

import com.klanting.signclick.storageLayer.entities.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public class JsonProvider implements StorageProvider{
    @Override
    public void init(JavaPlugin plugin) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public boolean store(Entity entity) {
        return false;
    }
}

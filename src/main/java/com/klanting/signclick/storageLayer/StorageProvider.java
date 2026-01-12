package com.klanting.signclick.storageLayer;

import com.klanting.signclick.storageLayer.entities.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public interface StorageProvider {
    void init(JavaPlugin plugin);
    void shutdown();

    boolean store(Entity entity);
}

package com.klanting.signclick.storageLayer;

import com.klanting.signclick.storageLayer.entities.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Type;

public interface StorageProvider {
    void init(JavaPlugin plugin);
    void shutdown();

    <E extends Entity> boolean store(Entity entity, Class<E> type);

    <T, E extends Entity> E getByKey(T key, Class<E> type);
}

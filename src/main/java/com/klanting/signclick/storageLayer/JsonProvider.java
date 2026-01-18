package com.klanting.signclick.storageLayer;

import com.google.common.reflect.TypeToken;
import com.klanting.signclick.logicLayer.companyLogic.Company;
import com.klanting.signclick.storageLayer.entities.Entity;
import com.klanting.signclick.storageLayer.entities.company.CompanyEntity;
import com.klanting.signclick.utils.Utils;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class JsonProvider implements StorageProvider{

    private final Map<Class<?>, Map<Object, Object>> storage = new HashMap<>();

    @Override
    public void init(JavaPlugin plugin) {
        Map<Object, Object> companies = Utils.readSave("companies", new TypeToken<HashMap<String, CompanyEntity>>(){}.getType(), new HashMap<>());
        storage.put(CompanyEntity.class, companies);

    }

    @Override
    public void shutdown() {

        Map<Object, Object> companies = storage.getOrDefault(CompanyEntity.class, new HashMap<>());
        Utils.writeSave("companies", companies);
    }

    @Override
    public <E extends Entity> boolean store(Entity entity, Class<E> type) {
        Map<Object, Object> typedMap = storage.getOrDefault(type, new HashMap<>());
        if (typedMap == null) {
            typedMap = new HashMap<>();
        }
        typedMap.put(entity.getKey(), entity);

        storage.put(type, typedMap);

        return true;
    }

    @Override
    public <T, E extends Entity> E getByKey(T key, Class<E> type) {
        Map<Object, Object> typedMap = storage.get(type);
        if (typedMap == null) {
            return null;
        }
        return type.cast(typedMap.get(key));
    }
}

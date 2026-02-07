package com.klanting.signclick.utils.statefullSQLSerializers;

import com.klanting.signclick.utils.statefullSQL.SQLSerializer;
import org.bukkit.Material;

public class MaterialSerializer extends SQLSerializer<Material> {

    public MaterialSerializer(Class<Material> type) {
        super(type);
    }

    @Override
    public String serialize(Material value) {
        return value.toString();
    }

    @Override
    public Material deserialize(String value) {
        return Material.getMaterial(value);
    }
}

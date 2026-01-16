package com.klanting.signclick.storageLayer.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.bukkit.Material;

@Converter(autoApply = true)
public class MaterialConverter implements AttributeConverter<Material, String> {

    @Override
    public String convertToDatabaseColumn(Material material) {
        return material == null ? null : material.name();
    }

    @Override
    public Material convertToEntityAttribute(String dbValue) {
        return dbValue == null ? null : Material.valueOf(dbValue);
    }
}

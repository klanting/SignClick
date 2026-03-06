package com.klanting.signclick.utils.statefulSQLSerializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klanting.signclick.utils.statefulSQL.SQLSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemStackSerializer extends SQLSerializer<ItemStack> {
    public ItemStackSerializer(Class<ItemStack> type) {
        super(type);
    }


    @Override
    public String serialize(ItemStack value) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(value.serialize());
        }catch (Exception e){
            throw new RuntimeException("Failuree to serialize ItemStack");
        }
    }

    @Override
    public ItemStack deserialize(String value) {
        return null;
    }
}

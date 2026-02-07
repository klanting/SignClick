package com.klanting.signclick.utils.statefullSQL.defaultSerializers;

import com.klanting.signclick.utils.statefullSQL.SQLSerializer;

import java.util.UUID;

public class UUIDSerializer extends SQLSerializer {
    public UUIDSerializer(Class type) {
        super(type);
    }

    @Override
    public String serialize(Object value) {
        return value.toString();
    }

    @Override
    public Object deserialize(String value) {
        return UUID.fromString(value);
    }
}

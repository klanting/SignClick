package com.klanting.signclick.utils.statefullSQL.defaultSerializers;

import com.klanting.signclick.utils.statefullSQL.SQLSerializer;

import java.util.UUID;

public class IntSerializer extends SQLSerializer {
    public IntSerializer(Class type) {
        super(type);
    }

    @Override
    public String serialize(Object value) {
        return value.toString();
    }

    @Override
    public Object deserialize(String value) {
        return Integer.valueOf(value);
    }
}

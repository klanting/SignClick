package com.klanting.signclick.utils.statefullSQL.defaultSerializers;

import com.klanting.signclick.utils.statefullSQL.SQLSerializer;

public class BoolSerializer extends SQLSerializer {
    public BoolSerializer(Class type) {
        super(type);
    }

    @Override
    public String serialize(Object value) {
        return value.toString();
    }

    @Override
    public Object deserialize(String value) {
        return Boolean.valueOf(value);
    }
}

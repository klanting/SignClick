package com.klanting.signclick.utils.statefulSQL.defaultSerializers;

import com.klanting.signclick.utils.statefulSQL.SQLSerializer;

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

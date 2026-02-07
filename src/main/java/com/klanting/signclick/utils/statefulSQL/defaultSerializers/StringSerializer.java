package com.klanting.signclick.utils.statefulSQL.defaultSerializers;

import com.klanting.signclick.utils.statefulSQL.SQLSerializer;


public class StringSerializer extends SQLSerializer {
    public StringSerializer (Class type) {
        super(type);
    }

    @Override
    public String serialize(Object value) {
        return (String) value;
    }

    @Override
    public Object deserialize(String value) {
        return value;
    }
}

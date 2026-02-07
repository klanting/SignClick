package com.klanting.signclick.utils.statefullSQL.defaultSerializers;

import com.klanting.signclick.utils.statefullSQL.SQLSerializer;
import io.ebeaninternal.server.util.Str;

import java.util.UUID;


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

package com.klanting.signclick.utils.statefullSQL.defaultSerializers;

import com.klanting.signclick.utils.statefullSQL.SQLSerializer;

public class DoubleSerializer extends SQLSerializer<Double> {
    public DoubleSerializer(Class type) {
        super(type);
    }

    @Override
    public String serialize(Double value) {
        return value.toString();
    }

    @Override
    public Double deserialize(String value) {
        return Double.valueOf(value);
    }
}

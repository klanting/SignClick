package com.klanting.signclick.utils.statefulSQL.defaultSerializers;

import com.klanting.signclick.utils.statefulSQL.SQLSerializer;

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

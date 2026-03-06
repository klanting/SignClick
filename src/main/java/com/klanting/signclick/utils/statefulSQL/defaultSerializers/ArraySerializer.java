package com.klanting.signclick.utils.statefulSQL.defaultSerializers;

import com.klanting.signclick.utils.statefulSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefulSQL.SQLSerializer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class ArraySerializer extends SQLSerializer {
    public ArraySerializer(Class type) {
        super(type);
    }

    @Override
    public String serialize(Object value) {
        List<Object> list = new ArrayList<>();

        int length = Array.getLength(value);
        for (int i = 0; i < length; i++) {
            list.add(Array.get(value, i));
        }

        return DatabaseSingleton.getInstance().serialize(List.class, list);
    }

    @Override
    public Object deserialize(String value) {
        List<Object> list = DatabaseSingleton.getInstance().deserialize(List.class, value);
        return list.toArray();
    }
}

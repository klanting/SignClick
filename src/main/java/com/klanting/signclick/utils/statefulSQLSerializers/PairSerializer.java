package com.klanting.signclick.utils.statefulSQLSerializers;

import com.klanting.signclick.utils.statefulSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefulSQL.SQLSerializer;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class PairSerializer extends SQLSerializer<Pair> {
    public PairSerializer(Class<Pair> type) {
        super(type);
    }

    @Override
    public String serialize(Pair value) {
        return DatabaseSingleton.getInstance().serialize(List.class, List.of(value.getLeft(), value.getRight()));
    }

    @Override
    public Pair deserialize(String value) {
        List<Object> o = DatabaseSingleton.getInstance().deserialize(List.class, value);
        return Pair.of(o.get(0), o.get(1));
    }
}

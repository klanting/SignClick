package com.klanting.signclick.utils.statefullSQLSerializers;

import com.klanting.signclick.utils.statefullSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefullSQL.SQLSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;

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

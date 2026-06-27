package com.klanting.signclick.utils.statefulSQL;

import com.klanting.signclick.utils.statefulSQL.defaultSerializers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SerializeManager {
    /**
     * Class to deal with serializing classes for SQL.
     * */

    private final List<SQLSerializer> serializers = new ArrayList<>();

    public void registerSerializer(SQLSerializer serializer){
        serializers.add(serializer);
    }

    public SerializeManager(){
        serializers.add(new UUIDSerializer(UUID.class));
        serializers.add(new IntSerializer(Integer.class));
        serializers.add(new BoolSerializer(Boolean.class));
        serializers.add(new MapSerializer(Map.class));
        serializers.add(new ListSerializer(List.class));
        serializers.add(new StringSerializer(String.class));
        serializers.add(new DoubleSerializer(Double.class));
    }

    public <S> String serialize(Class<?> type, S value){
        if (value == null){
            return null;
        }

        if (type.isArray()){
            ArraySerializer ar = new ArraySerializer(null);
            return ar.serialize(value);
        }

        for (SQLSerializer s: serializers){
            if (s.getType().equals(type) || s.getType().isAssignableFrom(type)){
                return s.serialize(value);
            }
        }

        throw new RuntimeException("Serialized doesn't exist for "+type);
    }

    public <S> boolean hasSerializer(Class<S> type){

        if (type.isArray()){
            return true;
        }

        for (SQLSerializer s: serializers){
            if (s.getType().equals(type) || s.getType().isAssignableFrom(type)){
                return true;
            }
        }
        return false;
    }

    public <S> S deserialize(Class<?> type, String value){

        if (value == null || value.equals("null")){
            return null;
        }

        if (type.isArray()){
            ArraySerializer ar = new ArraySerializer(null);
            return (S) ar.deserialize(value);
        }

        for (SQLSerializer s: serializers){
            if (s.getType().equals(type) || s.getType().isAssignableFrom(type)){
                return (S) s.deserialize(value);
            }
        }
        throw new RuntimeException("Serialized doesn't exist for "+type);
    }
}

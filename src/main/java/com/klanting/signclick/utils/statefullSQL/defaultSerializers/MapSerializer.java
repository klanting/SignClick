package com.klanting.signclick.utils.statefullSQL.defaultSerializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klanting.signclick.utils.statefullSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefullSQL.SQLSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MapSerializer extends SQLSerializer {


    public MapSerializer(Class<?> type) {
        super(type);
    }

    @Override
    public String serialize(Object value) {

        if (!(value instanceof Map<?, ?> mapFull)){
            throw new RuntimeException("Some weird serialization error for MAP");
        }


        Map<String, String> map = new HashMap<>();

        for(Map.Entry<?, ?> entry: mapFull.entrySet()){
            String sKey = DatabaseSingleton.getInstance().serialize(entry.getKey().getClass(), entry.getKey());

            String sVal = null;
            if (entry.getValue() != null){
                sVal = DatabaseSingleton.getInstance().serialize(entry.getValue().getClass(), entry.getValue());
            }

            map.put(sKey, sVal);
        }

        ObjectMapper mapper = new ObjectMapper();

        if (map.isEmpty()){
            try{
                return mapper.writeValueAsString(map);
            }catch (Exception e){
                throw new RuntimeException("WEIRD ISSUE MAP");
            }
        }


        Map.Entry<?, ?> firstEntry = mapFull.entrySet().iterator().next();

        Class<?> keyClass = firstEntry.getKey().getClass();
        Class<?> valueClass = firstEntry.getValue().getClass();

        map.put("autoFlushKeyClass", keyClass.getName());
        map.put("autoFlushValueClass", valueClass.getName());

        try{
            return mapper.writeValueAsString(map);
        }catch (Exception e){
            throw new RuntimeException("WEIRD ISSUE MAP");
        }
    }

    @Override
    public Object deserialize(String value) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            Map<String, String> stringMap = mapper.readValue(
                    value,
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {}
            );

            if(stringMap.isEmpty()){
                return new HashMap<>();
            }

            Map<Object, Object> result = new HashMap<>();

            Class<?> keyClass = Class.forName(stringMap.get("autoFlushKeyClass"));
            Class<?> valueClass = Class.forName(stringMap.get("autoFlushValueClass"));

            for (Map.Entry<String, String> entry : stringMap.entrySet()) {
                if(entry.getKey().equals("autoFlushKeyClass") || entry.getKey().equals("autoFlushValueClass")){
                    continue;
                }

                Object key = DatabaseSingleton.getInstance()
                        .deserialize(keyClass, entry.getKey());

                Object val = DatabaseSingleton.getInstance()
                        .deserialize(valueClass, entry.getValue());

                result.put(key, val);
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException("MAP DESERIALIZATION FAILED", e);
        }
    }
}

package com.klanting.signclick.utils.statefulSQL.defaultSerializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klanting.signclick.utils.statefulSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefulSQL.SQLSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListSerializer extends SQLSerializer {
    public ListSerializer(Class<?> type) {
        super(type);

    }

    @Override
    public String serialize(Object value) {

        if (!(value instanceof List<?> list)){
            throw new RuntimeException("Some weird serialization error for MAP");
        }

        List<String> list2 = new ArrayList<>();

        for(Object j: list){
            String sKey;
            if (j != null){
                sKey = DatabaseSingleton.getInstance().serialize(j.getClass(), j);
            }else{
                sKey = "null";
            }

            list2.add(sKey);
        }

        Map<String, Object> map = new HashMap<>();

        ObjectMapper mapper = new ObjectMapper();

        if (list.isEmpty()){
            try{
                return mapper.writeValueAsString(map);
            }catch (Exception e){
                throw new RuntimeException("WEIRD ISSUE MAP");
            }
        }

        // Find the class of the item
        // When list of value null, child type is Object
        Class<?> listClass = Object.class;
        for (int i=0; i<list.size(); i++){
            if (list.get(0) == null){
                continue;
            }
            listClass = list.get(0).getClass();
            break;
        }

        map.put("autoFlushListClass", listClass.getName());
        map.put("list", list2);


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
            Map<String, Object> stringMap = mapper.readValue(
                    value,
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {}
            );

            if(stringMap.isEmpty()){
                return new ArrayList<>();
            }

            List<Object> result = new ArrayList<>();

            Class<?> listClass = Class.forName((String) stringMap.get("autoFlushListClass"));

            List<String> stringList = (List<String>) stringMap.get("list");
            for(String s: stringList){
                Object addToResult = DatabaseSingleton.getInstance().deserialize(listClass, s);

                result.add(addToResult);
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException("MAP DESERIALIZATION FAILED", e);
        }
    }
}

package com.klanting.signclick.utils.Serializers;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class  LocalDateSerializer implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    /*
     * SerializeCompany to Gson
     * */

    @Override
    public JsonElement serialize(LocalDate dateTime, Type type, JsonSerializationContext context) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return context.serialize(dateTime.format(formatter));
    }

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(json.getAsString(), formatter);
    }

}
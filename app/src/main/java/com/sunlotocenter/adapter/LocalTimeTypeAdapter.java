package com.sunlotocenter.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.ISODateTimeFormat;

import java.lang.reflect.Type;

public class LocalTimeTypeAdapter implements JsonSerializer<LocalTime>,
        JsonDeserializer<LocalTime> {
    @Override
    public LocalTime deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {
        return LocalTime.parse(json.getAsString());
    }

    @Override
    public JsonElement serialize(LocalTime src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        return new JsonPrimitive(ISODateTimeFormat.time()
                .print(src));
    }
}

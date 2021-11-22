package com.sunlotocenter.adapter;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.sunlotocenter.dao.Entity;

import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;

public class UserTypeAdapter<T extends Entity> implements JsonDeserializer<T>, JsonSerializer<T> {
    private static final String TAG = "com.woolib.PartTypeAd";

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        Log.i(TAG, "We start serializing the partener");
        return context.serialize(new ModelMapper().map(src, src.getClassType()));
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Log.i(TAG, "deserialize: Start deserializing : " + json);
        JsonObject jsonObject= json.getAsJsonObject();
        String type= jsonObject.get("classType").getAsString();
        try {
            return context.deserialize(json, Class.forName(type));
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown element type: " + type, e);
        }
    }
}

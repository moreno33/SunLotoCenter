package com.sunlotocenter.adapter;

import java.lang.reflect.Type;

import org.modelmapper.ModelMapper;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.sunlotocenter.dao.Game;
import com.sunlotocenter.dao.User;

public class GameTypeAdapter<T extends Game> implements JsonDeserializer<T>
, JsonSerializer<T>
{
    
    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
    	return context.serialize(new ModelMapper().map(src, src.getClassType())); 
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject= json.getAsJsonObject();
        String type= jsonObject.get("classType").getAsString();
        try {
            return context.deserialize(json, Class.forName(type));
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown element type: " + type, e);
        }
    }
}
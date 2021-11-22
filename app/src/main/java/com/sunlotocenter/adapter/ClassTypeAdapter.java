package com.sunlotocenter.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ClassTypeAdapter extends  TypeAdapter<Class<?>> {

    @Override
    public void write(JsonWriter out, Class<?> value) throws IOException {
        if(value== null){
            out.nullValue();
            return;
        }
        out.value(value.getName());
    }

    @Override
    public Class<?> read(JsonReader in) throws IOException {
        if(in.peek()== JsonToken.NULL){
            in.nextNull();
            return null;
        }
        Class<?> cl = null;
        try {
            cl= cl.forName(in.nextString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cl;
    }
}

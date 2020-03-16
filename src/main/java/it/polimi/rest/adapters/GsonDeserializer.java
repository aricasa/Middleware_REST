package it.polimi.rest.adapters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Request;

public class GsonDeserializer<T> implements Deserializer<T> {

    private final Class<T> clazz;

    private final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public GsonDeserializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T parse(Request request) {
        return gson.fromJson(request.body(), clazz);
    }

}

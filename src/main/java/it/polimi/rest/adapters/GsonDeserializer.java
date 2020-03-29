package it.polimi.rest.adapters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import spark.Request;

/**
 * Deserialize the data by populating the object fields annotated with {@link Expose}.
 *
 * @param <T> result class
 */
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
        String body = request.body();
        return gson.fromJson(body.isEmpty() ? "{}" : body, clazz);
    }

}

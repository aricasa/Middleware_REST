package it.polimi.rest.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;
import java.util.Objects;

public abstract class Id {

    @Expose
    private final String id;

    public Id(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Id id1 = (Id) o;
        return id.equals(id1.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }

    public static class Serializer implements JsonSerializer<Id> {

        @Override
        public JsonElement serialize(Id src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.id);
        }

    }

}

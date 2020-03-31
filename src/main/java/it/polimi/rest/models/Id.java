package it.polimi.rest.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;

import java.lang.reflect.Type;
import java.util.Objects;

import static java.util.UUID.randomUUID;

public abstract class Id {

    @Expose
    protected final String id;

    public Id(String id) {
        this.id = id;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !Id.class.isAssignableFrom(o.getClass())) return false;
        Id id1 = (Id) o;
        return id.equals(id1.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }

    /**
     * Get a 16 chars random string.
     *
     * @return random string
     */
    public static String randomizer() {
        return randomUUID().toString()
                .replace("-", "")
                .substring(0, 16);
    }

    public static class Adapter implements JsonSerializer<Id> {

        @Override
        public JsonElement serialize(Id src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.id);
        }

    }

}

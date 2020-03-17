package it.polimi.rest.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;
import java.util.*;

@JsonAdapter(UserId.JsonAdapter.class)
public class UserId implements Model {

    @Expose
    private final String id;

    public UserId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return id.equals(userId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public Optional<String> self() {
        return Optional.of("/users/" + id);
    }

    @Override
    public Map<String, Link> links() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> embedded() {
        return Collections.emptyMap();
    }

    public Optional<String> images() {
        return self().map(url -> url + "/images");
    }

    public static class JsonAdapter implements JsonSerializer<UserId> {

        @Override
        public JsonElement serialize(UserId src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.id);
        }

    }

}

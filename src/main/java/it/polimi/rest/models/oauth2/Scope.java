package it.polimi.rest.models.oauth2;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

// TODO: allowed scopes list
@JsonAdapter(Scope.Adapter.class)
public class Scope {

    public static final String READ_USER = "read_user";
    public static final String READ_IMAGES = "read_images";

    public final String scope;

    public Scope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return scope;
    }

    public static class Adapter implements JsonSerializer<Scope> {

        @Override
        public JsonElement serialize(Scope src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.scope);
        }

    }

}

package it.polimi.rest.models.oauth2;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import it.polimi.rest.exceptions.OAuth2Exception;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

@JsonAdapter(Scope.Adapter.class)
public class Scope {

    public static final String READ_USER = "read_user";
    public static final String READ_IMAGES = "read_images";

    private static Collection<String> allowedScopes = new HashSet<>();

    static {
        allowedScopes.add(READ_USER);
        allowedScopes.add(READ_IMAGES);
    }

    public final String scope;

    public Scope(String scope) throws OAuth2Exception {
        if (!allowedScopes.contains(scope)) {
            throw new OAuth2Exception(OAuth2Exception.INVALID_SCOPE, "Unknown scope \"" + scope + "\"", null);
        }

        this.scope = scope;
    }

    @Override
    public String toString() {
        return scope;
    }

    public static Collection<Scope> convert(Collection<String> scopes) throws OAuth2Exception {
        Collection<Scope> result = new ArrayList<>();

        for (String scope : scopes) {
            result.add(new Scope(scope));
        }

        return result;
    }

    public static class Adapter implements JsonSerializer<Scope> {

        @Override
        public JsonElement serialize(Scope src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.scope);
        }

    }

}

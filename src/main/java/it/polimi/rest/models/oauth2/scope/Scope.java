package it.polimi.rest.models.oauth2.scope;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import it.polimi.rest.authorization.Agent;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.exceptions.oauth2.OAuth2BadRequestException;
import it.polimi.rest.exceptions.oauth2.OAuth2Exception;
import it.polimi.rest.models.User;
import it.polimi.rest.models.oauth2.OAuth2AccessToken;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@JsonAdapter(Scope.Adapter.class)
public abstract class Scope {

    public static final String READ_USER = "read_user";
    public static final String READ_IMAGES = "read_images";

    public final String scope;

    protected Scope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return scope;
    }

    public static Scope get(String scope) {
        if (READ_USER.equals(scope)) {
            return new ReadUser();

        } else if (READ_IMAGES.equals(scope)) {
            return new ReadImages();
        }

        throw new OAuth2BadRequestException(OAuth2Exception.INVALID_SCOPE, "Unknown scope \"" + scope + "\"", null);
    }

    public static Collection<Scope> convert(Collection<String> scopes) {
        return scopes.stream()
                .map(Scope::get)
                .collect(Collectors.toList());
    }

    protected abstract void addPermissions(Authorizer authorizer, DataProvider dataProvider, User.Id user, Agent agent);

    public final void addPermissions(Authorizer authorizer, DataProvider dataProvider, OAuth2AccessToken token) {
        addPermissions(authorizer, dataProvider, token.user, token);
    }

    public static class Adapter implements JsonSerializer<Scope> {

        @Override
        public JsonElement serialize(Scope src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.scope);
        }

    }

}

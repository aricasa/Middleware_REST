package it.polimi.rest.models.oauth2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import it.polimi.rest.authorization.SecuredObject;
import it.polimi.rest.models.Id;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@JsonAdapter(OAuth2AuthorizationCode.Adapter.class)
public class OAuth2AuthorizationCode extends Id implements SecuredObject {

    public final OAuth2ClientId client;
    public final Collection<Scope> scope;

    public OAuth2AuthorizationCode(String id, OAuth2ClientId client, Collection<Scope> scope) {
        super(id);
        this.client = client;
        this.scope = Collections.unmodifiableCollection(new ArrayList<>(scope));
    }

    static class Adapter implements JsonSerializer<OAuth2AuthorizationCode> {

        @Override
        public JsonElement serialize(OAuth2AuthorizationCode src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.add("code", context.serialize(src.id));
            return json;
        }

    }

}

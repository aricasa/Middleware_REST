package it.polimi.rest.models.oauth2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import it.polimi.rest.authorization.Agent;
import it.polimi.rest.authorization.Token;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.User;
import it.polimi.rest.models.oauth2.scope.Scope;

import java.lang.reflect.Type;
import java.util.*;

@JsonAdapter(OAuth2AccessToken.Adapter.class)
public class OAuth2AccessToken implements Token, Agent {

    @Expose
    public final Id id;

    private final Calendar expiration;

    public final User.Id user;
    public final Collection<Scope> scope;

    public OAuth2AccessToken(Id id, int lifeTime, User.Id user, Collection<Scope> scope) {
        this.id = id;

        this.expiration = Calendar.getInstance();
        this.expiration.add(Calendar.SECOND, lifeTime);

        this.user = user;
        this.scope = Collections.unmodifiableCollection(new ArrayList<>(scope));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OAuth2AccessToken that = (OAuth2AccessToken) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public TokenId id() {
        return id;
    }

    @Override
    public Agent agent() {
        return this;
    }

    @Override
    public boolean isValid() {
        Calendar now = Calendar.getInstance();
        return now.before(expiration);
    }

    @JsonAdapter(Id.Adapter.class)
    public static class Id extends TokenId {

        public Id(String id) {
            super(id);
        }

    }

    public static class Adapter implements JsonSerializer<OAuth2AccessToken> {

        @Override
        public JsonElement serialize(OAuth2AccessToken src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();

            json.add("access_token", context.serialize(src.id));
            json.add("token_type", context.serialize("bearer"));

            Calendar now = Calendar.getInstance();
            int expiration = (int) ((src.expiration.getTimeInMillis() - now.getTimeInMillis()) / 1000);
            json.add("expires_in", context.serialize(expiration));

            return json;
        }

    }

}

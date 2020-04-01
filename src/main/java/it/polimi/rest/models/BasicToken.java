package it.polimi.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import it.polimi.rest.authorization.Agent;
import it.polimi.rest.authorization.Token;
import it.polimi.rest.data.DataProvider;

import java.util.*;

public class BasicToken implements Token, Model {

    @Expose
    public final Id id;

    @Expose(deserialize = false)
    private final Calendar expiration;

    public final User.Id user;

    public BasicToken(Id id, int lifeTime, User.Id user) {
        this.id = id;

        this.expiration = Calendar.getInstance();
        this.expiration.add(Calendar.SECOND, lifeTime);

        this.user = user;
    }

    @Override
    public String toString() {
        return id.toString();
    }

    @Override
    public TokenId id() {
        return id;
    }

    @Override
    public Agent agent() {
        return user;
    }

    @Override
    public boolean isValid() {
        Calendar now = Calendar.getInstance();
        return now.before(expiration);
    }

    @Override
    public void onExpiration(DataProvider dataProvider) {
        dataProvider.remove(id);
    }

    @Override
    public Optional<String> self() {
        return Optional.of("/sessions/" + id);
    }

    @Override
    public Map<String, Link> links() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> embedded() {
        return Collections.emptyMap();
    }

    @JsonAdapter(Id.Adapter.class)
    public static class Id extends TokenId {

        public Id(String id) {
            super(id);
        }

    }

}

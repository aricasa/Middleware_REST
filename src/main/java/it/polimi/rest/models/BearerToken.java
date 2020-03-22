package it.polimi.rest.models;

import com.google.gson.annotations.Expose;
import it.polimi.rest.authorization.Agent;
import it.polimi.rest.authorization.Permission;
import it.polimi.rest.authorization.SecuredObject;
import it.polimi.rest.authorization.Token;

import java.util.*;

public class BearerToken implements Token, Model {

    @Expose
    public final TokenId id;

    @Expose(deserialize = false)
    private final Calendar expiration;

    public final UserId user;

    public BearerToken(TokenId id, int lifeTime,
                       UserId user) {

        this.id = id;

        this.expiration = Calendar.getInstance();
        this.expiration.add(Calendar.SECOND, lifeTime);

        this.user = user;
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

}

package it.polimi.rest.models;

import com.google.gson.annotations.Expose;

import java.util.*;

public class Token implements Model, TokenAcceptor {

    @Expose
    public final TokenId id;

    @Expose(deserialize = false)
    private final Calendar expiration;

    public final UserId owner;
    public final UserId managed;

    public Token(TokenId id, int lifeTime, UserId owner, UserId managed) {
        this.id = id;

        this.expiration = Calendar.getInstance();
        this.expiration.add(Calendar.SECOND, lifeTime);

        this.owner = owner;
        this.managed = managed;
    }

    /**
     * Check if the token is still valid.
     *
     * @return whether the token is valid (true) or has expired (false)
     */
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

    @Override
    public boolean accept(Token token) {
        return token.id.equals(id) && token.owner.equals(owner);
    }

    public boolean hasAccess(TokenAcceptor acceptor) {
        return isValid() && acceptor.accept(this);
    }

}

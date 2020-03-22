package it.polimi.rest.models;

import it.polimi.rest.authorization.Agent;
import it.polimi.rest.authorization.Token;

import java.util.Objects;

public class OAuthAccessToken implements Token, Agent {

    public final TokenId id;

    public OAuthAccessToken(TokenId id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OAuthAccessToken that = (OAuthAccessToken) o;
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
        return true;
    }

}

package it.polimi.rest.models.oauth2;

import it.polimi.rest.authorization.Agent;
import it.polimi.rest.authorization.Token;
import it.polimi.rest.models.TokenId;

import java.util.Objects;

public class OAuth2AccessToken implements Token, Agent {

    public final TokenId id;

    public OAuth2AccessToken(TokenId id) {
        this.id = id;
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
        return true;
    }

}

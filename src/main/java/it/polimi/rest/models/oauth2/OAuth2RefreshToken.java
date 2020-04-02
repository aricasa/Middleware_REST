package it.polimi.rest.models.oauth2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import it.polimi.rest.authorization.Agent;
import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.authorization.Token;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.User;
import it.polimi.rest.models.oauth2.scope.Scope;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

public class OAuth2RefreshToken implements Token {

    @Expose
    public final Id id;

    public final OAuth2AccessToken.Id accessToken;
    public final OAuth2Client.Id client;
    public final User.Id user;
    public final Collection<Scope> scope;

    public OAuth2RefreshToken(OAuth2RefreshToken.Id id,
                              OAuth2AccessToken.Id accessToken,
                              OAuth2Client.Id client,
                              User.Id user,
                              Collection<Scope> scope) {

        this.id = id;
        this.accessToken = accessToken;
        this.client = client;
        this.user = user;
        this.scope = new ArrayList<>(scope);
    }

    @Override
    public TokenId id() {
        return id;
    }

    @Override
    public Agent agent() {
        return id;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void onExpiration(DataProvider dataProvider, SessionManager sessionManager) {

    }

    @JsonAdapter(Id.Adapter.class)
    public static class Id extends TokenId implements Agent {

        public Id(String id) {
            super(id);
        }

    }

}

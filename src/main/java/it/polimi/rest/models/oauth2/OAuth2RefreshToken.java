package it.polimi.rest.models.oauth2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import it.polimi.rest.authorization.Agent;
import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.authorization.Token;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.models.TokenId;

import java.util.Calendar;

public class OAuth2RefreshToken implements Token {

    @Expose
    public final Id id;

    private final Calendar expiration;

    public OAuth2RefreshToken(OAuth2RefreshToken.Id id, int lifeTime) {
        this.id = id;

        this.expiration = Calendar.getInstance();
        this.expiration.add(Calendar.SECOND, lifeTime);
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
        Calendar now = Calendar.getInstance();
        return now.before(expiration);
    }

    @Override
    public void onExpiration(DataProvider dataProvider, SessionManager sessionManager) {
        //dataProvider.remove(id);
    }

    @JsonAdapter(Id.Adapter.class)
    public static class Id extends TokenId implements Agent {

        public Id(String id) {
            super(id);
        }

    }

}

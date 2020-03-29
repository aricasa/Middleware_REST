package it.polimi.rest.models.oauth2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import it.polimi.rest.authorization.Agent;
import it.polimi.rest.authorization.SecuredObject;
import it.polimi.rest.authorization.Token;
import it.polimi.rest.models.Id;
import it.polimi.rest.models.TokenId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

public class OAuth2AuthorizationCode {

    @Expose
    @SerializedName("code")
    public final Id id;

    public final OAuth2Client.Id client;
    public final String redirectUri;
    public final Collection<Scope> scope;
    public final Calendar expiration;

    public OAuth2AuthorizationCode(Id id, OAuth2Client.Id client, String redirectUri, Collection<Scope> scope) {
        this.id = id;
        this.client = client;
        this.redirectUri = redirectUri;
        this.scope = Collections.unmodifiableCollection(new ArrayList<>(scope));

        this.expiration = Calendar.getInstance();
        this.expiration.add(Calendar.MINUTE, 10);
    }

    public boolean isValid() {
        Calendar now = Calendar.getInstance();
        return now.before(expiration);
    }

    @JsonAdapter(Id.Adapter.class)
    public static class Id extends it.polimi.rest.models.Id implements SecuredObject {

        public Id(String id) {
            super(id);
        }

    }

}

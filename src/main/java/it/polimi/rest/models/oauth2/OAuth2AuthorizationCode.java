package it.polimi.rest.models.oauth2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import it.polimi.rest.authorization.SecuredObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class OAuth2AuthorizationCode {

    @Expose
    @SerializedName("code")
    public final Id id;

    public final OAuth2Client.Id client;
    public final Collection<Scope> scope;

    public OAuth2AuthorizationCode(Id id, OAuth2Client.Id client, Collection<Scope> scope) {
        this.id = id;
        this.client = client;
        this.scope = Collections.unmodifiableCollection(new ArrayList<>(scope));
    }

    @JsonAdapter(Id.Adapter.class)
    public static class Id extends it.polimi.rest.models.Id implements SecuredObject {

        public Id(String id) {
            super(id);
        }

    }

}

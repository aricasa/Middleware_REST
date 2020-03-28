package it.polimi.rest.models.oauth2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import it.polimi.rest.authorization.SecuredObject;
import it.polimi.rest.models.Id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

public class OAuth2AuthorizationCode {

    @Expose
    @SerializedName("code")
    public final OAuth2AuthorizationCodeId id;

    public final OAuth2ClientId client;
    public final Collection<Scope> scope;

    public OAuth2AuthorizationCode(OAuth2AuthorizationCodeId id, OAuth2ClientId client, Collection<Scope> scope) {
        this.id = id;
        this.client = client;
        this.scope = Collections.unmodifiableCollection(new ArrayList<>(scope));
    }

    public static Function<String, OAuth2AuthorizationCodeId> idSupplier() {
        return OAuth2AuthorizationCodeId::new;
    }

    @JsonAdapter(Id.Adapter.class)
    public static class OAuth2AuthorizationCodeId extends Id implements SecuredObject {

        public OAuth2AuthorizationCodeId(String id) {
            super(id);
        }

    }

}

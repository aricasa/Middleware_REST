package it.polimi.rest.models.oauth2;

import com.google.gson.annotations.JsonAdapter;
import it.polimi.rest.models.Id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@JsonAdapter(Id.Serializer.class)
public class OAuth2AuthorizationCode extends Id {

    public final Collection<Scope> scopes;

    public OAuth2AuthorizationCode(String id, Collection<Scope> scopes) {
        super(id);
        this.scopes = Collections.unmodifiableCollection(new ArrayList<>(scopes));
    }

}

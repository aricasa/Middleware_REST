package it.polimi.rest.models.oauth2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class OAuth2AuthorizationRequest {

    public final String responseType;
    public final OAuth2Client.Id client;
    public final String callback;
    public final Collection<String> scopes;
    public final String state;

    public OAuth2AuthorizationRequest(String responseType, OAuth2Client.Id client, String callback, Collection<String> scopes, String state) {
        this.responseType = responseType;
        this.client = client;
        this.callback = callback;
        this.scopes = Collections.unmodifiableCollection(new ArrayList<>(scopes));
        this.state = state;
    }

}

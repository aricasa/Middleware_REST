package it.polimi.rest.models.oauth2;

import com.google.gson.annotations.JsonAdapter;
import it.polimi.rest.models.Id;

@JsonAdapter(Id.Adapter.class)
public class OAuth2ClientSecret extends Id {

    public OAuth2ClientSecret(String id) {
        super(id);
    }

}

package it.polimi.rest.models.oauth2;

import com.google.gson.annotations.JsonAdapter;
import it.polimi.rest.authorization.Agent;
import it.polimi.rest.authorization.SecuredObject;
import it.polimi.rest.models.Id;

@JsonAdapter(Id.Serializer.class)
public class OAuth2ClientId extends Id implements Agent, SecuredObject {

    public OAuth2ClientId(String id) {
        super(id);
    }

}

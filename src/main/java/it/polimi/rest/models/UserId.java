package it.polimi.rest.models;

import com.google.gson.annotations.JsonAdapter;
import it.polimi.rest.authorization.Agent;
import it.polimi.rest.authorization.SecuredObject;

@JsonAdapter(Id.Serializer.class)
public class UserId extends Id implements Agent, SecuredObject {

    public UserId(String id) {
        super(id);
    }

}

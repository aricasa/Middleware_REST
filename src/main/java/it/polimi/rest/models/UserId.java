package it.polimi.rest.models;

import it.polimi.rest.authorization.Agent;
import it.polimi.rest.authorization.SecuredObject;

@com.google.gson.annotations.JsonAdapter(Id.Adapter.class)
public class UserId extends Id implements Agent, SecuredObject {

    public UserId(String id) {
        super(id);
    }

}

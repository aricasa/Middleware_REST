package it.polimi.rest.models;

import it.polimi.rest.authorization.SecuredObject;

@com.google.gson.annotations.JsonAdapter(Id.Adapter.class)
public class TokenId extends Id implements SecuredObject {

    public TokenId(String id) {
        super(id);
    }

}

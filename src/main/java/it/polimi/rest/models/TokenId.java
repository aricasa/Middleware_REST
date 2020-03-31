package it.polimi.rest.models;

import com.google.gson.annotations.JsonAdapter;
import it.polimi.rest.authorization.SecuredObject;

@JsonAdapter(Id.Adapter.class)
public class TokenId extends Id implements SecuredObject {

    public TokenId(String id) {
        super(id);
    }

}

package it.polimi.rest.models;

import com.google.gson.annotations.JsonAdapter;

@JsonAdapter(Id.Serializer.class)
public class TokenId extends Id {

    public TokenId(String id) {
        super(id);
    }

}

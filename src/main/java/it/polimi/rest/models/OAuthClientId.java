package it.polimi.rest.models;

import com.google.gson.annotations.JsonAdapter;

@JsonAdapter(Id.Serializer.class)
public class OAuthClientId extends Id {

    public OAuthClientId(String id) {
        super(id);
    }

}

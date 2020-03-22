package it.polimi.rest.models;

import com.google.gson.annotations.JsonAdapter;

@JsonAdapter(Id.Serializer.class)
public class OAuthClientSecret extends Id {

    public OAuthClientSecret(String id) {
        super(id);
    }

}

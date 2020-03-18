package it.polimi.rest.models;

import com.google.gson.annotations.JsonAdapter;

@JsonAdapter(Id.Serializer.class)
public class UserId extends Id {

    public UserId(String id) {
        super(id);
    }

}

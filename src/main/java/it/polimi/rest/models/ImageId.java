package it.polimi.rest.models;

import com.google.gson.annotations.JsonAdapter;

@JsonAdapter(Id.Serializer.class)
public class ImageId extends Id {

    public ImageId(String id) {
        super(id);
    }

}

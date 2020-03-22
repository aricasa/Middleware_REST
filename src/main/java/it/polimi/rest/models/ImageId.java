package it.polimi.rest.models;

import com.google.gson.annotations.JsonAdapter;
import it.polimi.rest.authorization.SecuredObject;

@JsonAdapter(Id.Serializer.class)
public class ImageId extends Id implements SecuredObject {

    public ImageId(String id) {
        super(id);
    }

}

package it.polimi.rest.models;

import it.polimi.rest.authorization.SecuredObject;

@com.google.gson.annotations.JsonAdapter(Id.Adapter.class)
public class ImageId extends Id implements SecuredObject {

    public ImageId(String id) {
        super(id);
    }

}

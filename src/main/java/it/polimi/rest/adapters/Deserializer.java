package it.polimi.rest.adapters;

import it.polimi.rest.models.TokenId;
import spark.Request;

public interface Deserializer<T> {

    /**
     * Convert the request parameters / payload to the entity it represents.
     *
     * @param request   request
     * @return deserialized data
     */
    T parse(Request request);

}

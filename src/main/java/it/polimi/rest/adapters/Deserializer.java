package it.polimi.rest.adapters;

import it.polimi.rest.models.TokenId;
import spark.Request;

public interface Deserializer<T> {

    T parse(Request request, TokenId token);

}

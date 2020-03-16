package it.polimi.rest.adapters;

import spark.Request;

public interface Deserializer<T> {

    T parse(Request request);

}

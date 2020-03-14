package it.polimi.rest.messages;

import java.util.Optional;

public interface Message {

    String APPLICATION_JSON = "application/json";

    int code();
    String type();
    Optional<Object> payload();

}

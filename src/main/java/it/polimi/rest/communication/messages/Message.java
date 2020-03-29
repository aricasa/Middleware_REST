package it.polimi.rest.communication.messages;

import java.util.Optional;

public interface Message {

    String TEXT_HTML = "text/html";
    String APPLICATION_JSON = "application/json";
    String APPLICATION_HAL_JSON = "application/hal+json";

    /**
     * Get the HTTP status code.
     *
     * @return status code
     */
    int code();

    /**
     * Get the value to be used for the Content-Type header.
     *
     * @return type
     */
    String type();

    /**
     * Get the body of the message.
     *
     * @return body
     */
    Optional<Object> payload();

}

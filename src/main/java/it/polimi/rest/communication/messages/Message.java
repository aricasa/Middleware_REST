package it.polimi.rest.communication.messages;

import java.util.Optional;

public interface Message {

    String TEXT_HTML = "text/html";
    String APPLICATION_JSON = "application/json";
    String APPLICATION_HAL_JSON = "application/hal+json";

    /**
     * Get the HTTP status code.
     * See https://tools.ietf.org/html/rfc2616#section-6.1.1 for more details
     *
     * @return status code
     */
    int code();

    /**
     * Get the value to be used for the Content-Type header.
     * See https://tools.ietf.org/html/rfc2616#section-14.17 for more details.
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

    /**
     * Get the cache control policy the receiver has to follow.
     * See https://tools.ietf.org/html/rfc2616#section-13.1.3 for more details.
     *
     * @return cache control header
     */
    default Optional<String> cacheControl() {
        return Optional.empty();
    }

    /**
     * Get the Pragma header to be set.
     * See https://tools.ietf.org/html/rfc2616#section-14.32 for more details.
     *
     * @return pragma header
     */
    default Optional<String> pragma() {
        return Optional.empty();
    }

}

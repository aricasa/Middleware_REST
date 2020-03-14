package it.polimi.rest.messages;

import java.util.Optional;

public class UserDeletionMessage implements Message {

    @Override
    public int code() {
        return HttpStatus.NO_CONTENT;
    }

    @Override
    public String type() {
        return APPLICATION_JSON;
    }

    @Override
    public Optional<Object> payload() {
        return Optional.empty();
    }

}

package it.polimi.rest.communication.messages;

import it.polimi.rest.communication.HttpStatus;

import java.util.Optional;

public class LogoutMessage implements Message {

    @Override
    public int code() {
        return HttpStatus.NO_CONTENT;
    }

    @Override
    public String type() {
        return null;
    }

    @Override
    public Optional<Object> payload() {
        return Optional.empty();
    }

}

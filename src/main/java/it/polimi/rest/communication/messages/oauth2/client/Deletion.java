package it.polimi.rest.communication.messages.oauth2.client;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.communication.messages.Message;

import java.util.Optional;

class Deletion implements Message {

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

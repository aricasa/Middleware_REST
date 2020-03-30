package it.polimi.rest.communication.messages.session;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.models.BearerToken;

import java.util.Optional;

class Creation implements Message {

    private final BearerToken token;

    public Creation(BearerToken token) {
        this.token = token;
    }

    @Override
    public int code() {
        return HttpStatus.CREATED;
    }

    @Override
    public String type() {
        return APPLICATION_HAL_JSON;
    }

    @Override
    public Optional<Object> payload() {
        return Optional.ofNullable(token);
    }

    @Override
    public Optional<String> cacheControl() {
        return Optional.of("no-store");
    }

    @Override
    public Optional<String> pragma() {
        return Optional.of("no-cache");
    }

}

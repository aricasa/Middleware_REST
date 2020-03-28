package it.polimi.rest.communication.messages.user;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.models.User;

import java.util.Optional;

class Details implements Message {

    private final User user;

    public Details(User user) {
        this.user = user;
    }

    @Override
    public int code() {
        return HttpStatus.OK;
    }

    @Override
    public String type() {
        return "application/hal+json";
    }

    @Override
    public Optional<Object> payload() {
        return Optional.ofNullable(user);
    }

}

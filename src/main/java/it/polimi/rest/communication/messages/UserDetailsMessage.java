package it.polimi.rest.communication.messages;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.models.User;

import java.util.Optional;

public class UserDetailsMessage implements Message {

    private final User user;

    public UserDetailsMessage(User user) {
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

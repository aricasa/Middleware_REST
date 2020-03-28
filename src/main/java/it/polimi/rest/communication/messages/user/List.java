package it.polimi.rest.communication.messages.user;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.models.UsersList;

import java.util.Optional;

class List implements Message {

    private final UsersList users;

    public List(UsersList users) {
        this.users = users;
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
        return Optional.ofNullable(users);
    }

}

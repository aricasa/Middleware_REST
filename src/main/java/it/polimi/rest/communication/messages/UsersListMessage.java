package it.polimi.rest.communication.messages;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.models.UsersList;

import java.util.*;

public class UsersListMessage implements Message {

    private final UsersList users;

    public UsersListMessage(UsersList users) {
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

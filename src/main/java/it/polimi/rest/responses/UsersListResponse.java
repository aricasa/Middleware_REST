package it.polimi.rest.responses;

import it.polimi.rest.models.User;

import java.util.Collection;
import java.util.Optional;

public final class UsersListResponse extends Response {

    public UsersListResponse(Collection<User> users) {
        super(200, APPLICATION_JSON, null);
        embed("users", users);
    }

    @Override
    public Optional<String> self() {
        return Optional.of("/users");
    }

}

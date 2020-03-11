package it.polimi.rest.responses;

import it.polimi.rest.models.User;

import java.util.Optional;

public final class UserDetailsResponse extends Response {

    private final User user;

    public UserDetailsResponse(User user) {
        super(200, APPLICATION_JSON, user);
        this.user = user;
    }

    @Override
    public Optional<String> self() {
        return Optional.of("/users/" + user.getUsername());
    }

}

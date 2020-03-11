package it.polimi.rest.responses;

import it.polimi.rest.models.User;

import java.util.Optional;

public final class SignupResponse extends Response {

    private final User user;

    public SignupResponse(User user) {
        super(201, APPLICATION_JSON, user);
        this.user = user;
    }

    @Override
    public Optional<String> self() {
        return Optional.of("/users/" + user.getUsername());
    }

}

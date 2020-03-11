package it.polimi.rest.responses;

import it.polimi.rest.authorization.Token;

import java.util.Map;
import java.util.Optional;

public final class LoginResponse extends Response {

    public LoginResponse(Token token) {
        super(201, APPLICATION_JSON, token);
    }

    @Override
    public Optional<String> self() {
        return Optional.empty();
    }

}

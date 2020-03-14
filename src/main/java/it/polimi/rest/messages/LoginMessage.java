package it.polimi.rest.messages;

import it.polimi.rest.models.Token;

import java.util.Optional;

public class LoginMessage implements Message {

    private final Token token;

    public LoginMessage(Token token) {
        this.token = token;
    }

    @Override
    public int code() {
        return HttpStatus.CREATED;
    }

    @Override
    public String type() {
        return APPLICATION_JSON;
    }

    @Override
    public Optional<Object> payload() {
        return Optional.ofNullable(token);
    }

}

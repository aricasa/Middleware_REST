package it.polimi.rest.communication.messages.oauth2.accesstoken;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.models.oauth2.OAuth2AccessToken;

import java.util.Optional;

class Creation implements Message {

    private final OAuth2AccessToken token;

    public Creation(OAuth2AccessToken token) {
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

    @Override
    public Optional<String> cacheControl() {
        return Optional.of("no-store");
    }

    @Override
    public Optional<String> pragma() {
        return Optional.of("no-cache");
    }

}

package it.polimi.rest.communication.messages.oauth2.client;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.models.oauth2.OAuth2Client;

import java.util.Optional;

class Details implements Message {

    private final OAuth2Client client;

    public Details(OAuth2Client client) {
        this.client = client;
    }

    @Override
    public int code() {
        return HttpStatus.OK;
    }

    @Override
    public String type() {
        return APPLICATION_HAL_JSON;
    }

    @Override
    public Optional<Object> payload() {
        return Optional.ofNullable(client);
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

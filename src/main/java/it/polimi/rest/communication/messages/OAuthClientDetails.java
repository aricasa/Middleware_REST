package it.polimi.rest.communication.messages;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.models.OAuthClient;

import java.util.Optional;

public class OAuthClientDetails implements Message {

    private final OAuthClient client;

    public OAuthClientDetails(OAuthClient client) {
        this.client = client;
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
        return Optional.of(client);
    }

}

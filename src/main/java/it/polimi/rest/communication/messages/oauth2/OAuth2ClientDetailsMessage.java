package it.polimi.rest.communication.messages.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.models.oauth2.OAuth2Client;

import java.util.Optional;

public class OAuth2ClientDetailsMessage implements Message {

    private final OAuth2Client client;

    public OAuth2ClientDetailsMessage(OAuth2Client client) {
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

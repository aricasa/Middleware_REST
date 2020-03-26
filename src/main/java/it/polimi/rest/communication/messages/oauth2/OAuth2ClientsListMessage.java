package it.polimi.rest.communication.messages.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.models.oauth2.OAuth2ClientsList;

import java.util.Optional;

public class OAuth2ClientsListMessage implements Message {

    private final OAuth2ClientsList clients;

    public OAuth2ClientsListMessage(OAuth2ClientsList clients) {
        this.clients = clients;
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
        return Optional.ofNullable(clients);
    }

}

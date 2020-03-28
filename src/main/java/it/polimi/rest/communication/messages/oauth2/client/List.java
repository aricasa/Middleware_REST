package it.polimi.rest.communication.messages.oauth2.client;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.models.oauth2.OAuth2ClientsList;

import java.util.Optional;

class List implements Message {

    private final OAuth2ClientsList clients;

    public List(OAuth2ClientsList clients) {
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

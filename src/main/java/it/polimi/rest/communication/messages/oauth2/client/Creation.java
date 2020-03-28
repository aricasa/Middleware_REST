package it.polimi.rest.communication.messages.oauth2.client;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.models.oauth2.OAuth2Client;

class Creation extends Details {

    public Creation(OAuth2Client client) {
        super(client);
    }

    @Override
    public int code() {
        return HttpStatus.CREATED;
    }

}

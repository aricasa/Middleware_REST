package it.polimi.rest.communication.messages.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.models.oauth2.OAuth2Client;

public class OAuth2ClientCreationMessage extends OAuth2ClientDetailsMessage {

    public OAuth2ClientCreationMessage(OAuth2Client client) {
        super(client);
    }

    @Override
    public int code() {
        return HttpStatus.CREATED;
    }

}

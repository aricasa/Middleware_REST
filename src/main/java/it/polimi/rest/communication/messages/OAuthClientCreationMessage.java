package it.polimi.rest.communication.messages;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.models.OAuthClient;

public class OAuthClientCreationMessage extends OAuthClientDetails {

    public OAuthClientCreationMessage(OAuthClient client) {
        super(client);
    }

    @Override
    public int code() {
        return HttpStatus.CREATED;
    }

}

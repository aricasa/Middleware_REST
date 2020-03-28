package it.polimi.rest.communication.messages.oauth2.client;

import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.OAuth2ClientsList;

public final class OAuth2ClientMessage {

    private OAuth2ClientMessage() {

    }

    public static Message details(OAuth2Client client) {
        return new Details(client);
    }

    public static Message creation(OAuth2Client client) {
        return new Creation(client);
    }

    public static Message deletion() {
        return new Deletion();
    }

    public static Message list(OAuth2ClientsList clients) {
        return new List(clients);
    }

}

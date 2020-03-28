package it.polimi.rest.communication.messages.session;

import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.models.BearerToken;

public final class SessionMessage {

    private SessionMessage() {

    }

    public static Message creation(BearerToken token) {
        return new Creation(token);
    }

    public static Message deletion() {
        return new Deletion();
    }

}

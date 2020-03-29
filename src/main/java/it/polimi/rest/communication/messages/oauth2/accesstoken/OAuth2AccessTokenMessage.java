package it.polimi.rest.communication.messages.oauth2.accesstoken;

import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.models.oauth2.OAuth2AccessToken;

public final class OAuth2AccessTokenMessage {

    private OAuth2AccessTokenMessage() {

    }

    public static Message creation(OAuth2AccessToken token) {
        return new Creation(token);
    }

}

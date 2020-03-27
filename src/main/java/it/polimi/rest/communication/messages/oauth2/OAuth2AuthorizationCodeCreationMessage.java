package it.polimi.rest.communication.messages.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.models.oauth2.OAuth2AuthorizationCode;

import java.util.Optional;

public class OAuth2AuthorizationCodeCreationMessage implements Message {

    private final OAuth2AuthorizationCode code;

    public OAuth2AuthorizationCodeCreationMessage(OAuth2AuthorizationCode code) {
        this.code = code;
    }

    @Override
    public int code() {
        return HttpStatus.CREATED;
    }

    @Override
    public String type() {
        return "application/json";
    }

    @Override
    public Optional<Object> payload() {
        return Optional.ofNullable(code);
    }

}

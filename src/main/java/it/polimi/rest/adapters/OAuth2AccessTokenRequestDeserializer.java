package it.polimi.rest.adapters;

import it.polimi.rest.models.oauth2.OAuth2AccessTokenRequest;
import spark.Request;

public class OAuth2AccessTokenRequestDeserializer implements Deserializer<OAuth2AccessTokenRequest> {

    @Override
    public OAuth2AccessTokenRequest parse(Request request) {
        return null;
    }

}

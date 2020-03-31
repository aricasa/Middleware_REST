package it.polimi.rest.api.oauth2;

import it.polimi.rest.authorization.Agent;
import it.polimi.rest.authorization.AuthorizationProxy;
import it.polimi.rest.authorization.Token;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.exceptions.oauth2.OAuth2BadRequestException;
import it.polimi.rest.models.BasicToken;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2AuthorizationRequest;
import it.polimi.rest.models.oauth2.OAuth2Client;

import static it.polimi.rest.exceptions.oauth2.OAuth2Exception.ACCESS_DENIED;
import static it.polimi.rest.exceptions.oauth2.OAuth2Exception.INVALID_REQUEST;

public class Deny extends Grant {

    public Deny(AuthorizationProxy proxy) {
        super(proxy);
    }

    @Override
    protected Message process(BasicToken.Id token, OAuth2AuthorizationRequest data) {
        OAuth2Client client = proxy.dataProvider(new Token() {
            @Override
            public TokenId id() {
                return null;
            }

            @Override
            public Agent agent() {
                return data.client;
            }

            @Override
            public boolean isValid() {
                return true;
            }
        }).oAuth2Client(data.client);

        if (!client.callback.equals(data.callback)) {
            // Invalid redirect URI.
            // As stated in the RFC, the user agent must not be redirected to it.
            throw new OAuth2BadRequestException(INVALID_REQUEST, "Redirect URI mismatch", null);
        }

        throw new OAuth2BadRequestException(ACCESS_DENIED, null, null).redirect(client.callback, data.state);
    }

}

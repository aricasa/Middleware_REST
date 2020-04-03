package it.polimi.rest.api.oauth2;

import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.TokenHeaderExtractor;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.communication.messages.oauth2.client.OAuth2ClientMessage;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.User;
import it.polimi.rest.models.oauth2.OAuth2ClientsList;
import spark.Request;

import java.util.Optional;

/**
 * Get the OAuth2 clients registered by a user.
 */
class Clients extends Responder<TokenId, String> {

    private final SessionManager sessionManager;

    public Clients(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected Optional<TokenExtractor<TokenId>> tokenExtractor() {
        return Optional.of(new TokenHeaderExtractor<>(TokenId::new));
    }

    @Override
    protected String deserialize(Request request) {
        return request.params("username");
    }

    @Override
    protected Message process(TokenId token, String username) {
        DataProvider dataProvider = sessionManager.dataProvider(token);
        User user = dataProvider.userByUsername(username);
        OAuth2ClientsList clients = dataProvider.oAuth2Clients(user.id);
        return OAuth2ClientMessage.list(clients);
    }

}

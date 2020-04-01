package it.polimi.rest.api.oauth2;

import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.TokenHeaderExtractor;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.communication.messages.oauth2.client.OAuth2ClientMessage;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.utils.Logger;
import spark.Request;

import java.util.Optional;

/**
 * Remove an OAuth2 client.
 */
class ClientRemove extends Responder<TokenId, OAuth2Client.Id> {

    private final Logger logger = new Logger(getClass());
    private final SessionManager sessionManager;

    public ClientRemove(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected Optional<TokenExtractor<TokenId>> tokenExtractor() {
        return Optional.of(new TokenHeaderExtractor<>(TokenId::new));
    }

    @Override
    protected OAuth2Client.Id deserialize(Request request) {
        return new OAuth2Client.Id(request.params("clientId"));
    }

    @Override
    protected Message process(TokenId token, OAuth2Client.Id clientId) {
        DataProvider dataProvider = sessionManager.dataProvider(token);
        OAuth2Client client = dataProvider.oAuth2Client(clientId);
        dataProvider.remove(client.id);

        logger.d("OAuth2 client " + client + " removed");
        return OAuth2ClientMessage.deletion();
    }

}

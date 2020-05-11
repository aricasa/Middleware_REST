package it.polimi.rest.api.oauth2;

import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.TokenHeaderExtractor;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.communication.messages.oauth2.client.OAuth2ClientMessage;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.exceptions.NotFoundException;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.utils.Logger;
import spark.Request;

import java.util.Optional;

/**
 * Get the details of an OAuth2 client.
 */
public class ClientDetails extends Responder<TokenId, ClientDetails.Data> {

    private final Logger logger = new Logger(getClass());
    private final SessionManager sessionManager;

    public ClientDetails(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected Optional<TokenExtractor<TokenId>> tokenExtractor() {
        return Optional.of(new TokenHeaderExtractor<>(TokenId::new));
    }

    @Override
    protected Data deserialize(Request request) {
        String username = request.params("username");
        OAuth2Client.Id clientId = new OAuth2Client.Id(request.params("clientId"));

        return new ClientDetails.Data(username, clientId);
    }

    @Override
    protected Message process(TokenId token, Data data) {
        DataProvider dataProvider = sessionManager.dataProvider(token);
        OAuth2Client client = dataProvider.oAuth2Client(data.clientId);

        if (!client.owner.username.equals(data.username)) {
            throw new NotFoundException();
        }

        return OAuth2ClientMessage.details(client);
    }

    protected static class Data {
        public final String username;
        public final OAuth2Client.Id clientId;

        public Data(String username, OAuth2Client.Id clientId) {
            this.username = username;
            this.clientId = clientId;
        }
    }

}

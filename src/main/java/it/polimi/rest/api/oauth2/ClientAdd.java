package it.polimi.rest.api.oauth2;

import it.polimi.rest.adapters.GsonDeserializer;
import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.TokenHeaderExtractor;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.communication.messages.oauth2.client.OAuth2ClientMessage;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.exceptions.BadRequestException;
import it.polimi.rest.models.Id;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.User;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.utils.Logger;
import spark.Request;

import java.util.Optional;

/**
 * Add a new OAuth2 client.
 */
class ClientAdd extends Responder<TokenId, ClientAdd.Data> {

    private final Logger logger = new Logger(getClass());
    private final SessionManager sessionManager;

    public ClientAdd(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected Optional<TokenExtractor<TokenId>> tokenExtractor() {
        return Optional.of(new TokenHeaderExtractor<>(TokenId::new));
    }

    @Override
    protected ClientAdd.Data deserialize(Request request) {
        OAuth2Client client = new GsonDeserializer<>(OAuth2Client.class).parse(request);

        if (client.name == null || client.name.trim().isEmpty()) {
            throw new BadRequestException("Name not specified");

        } else if (client.callback == null || client.callback.trim().isEmpty()) {
            throw new BadRequestException("Callback URI not specified");
        }

        return new Data(
                client,
                request.params("username")
        );
    }

    @Override
    protected Message process(TokenId token, ClientAdd.Data data) {
        DataProvider dataProvider = sessionManager.dataProvider(token);
        User user = dataProvider.userByUsername(data.username);

        OAuth2Client.Id id = dataProvider.uniqueId(Id::randomizer, OAuth2Client.Id::new);
        OAuth2Client.Secret secret = dataProvider.uniqueId(Id::randomizer, OAuth2Client.Secret::new);

        OAuth2Client client = new OAuth2Client(user, id, secret, data.client.name, data.client.callback);
        dataProvider.add(client);

        logger.d("OAuth2 client " + client + " added");
        return OAuth2ClientMessage.creation(client);
    }

    protected static class Data {
        public final OAuth2Client client;
        public final String username;

        public Data(OAuth2Client client, String username) {
            this.client = client;
            this.username = username;
        }
    }

}

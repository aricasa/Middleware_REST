package it.polimi.rest.api.main;

import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.TokenHeaderExtractor;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.communication.messages.user.UserMessage;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.UsersList;
import spark.Request;

import java.util.Optional;

/**
 * Get the users list.
 */
class Users extends Responder<TokenId, Void> {

    private final SessionManager sessionManager;

    public Users(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected Optional<TokenExtractor<TokenId>> tokenExtractor() {
        return Optional.of(new TokenHeaderExtractor<>(TokenId::new));
    }

    @Override
    protected Void deserialize(Request request) {
        return null;
    }

    @Override
    protected Message process(TokenId token, Void data) {
        DataProvider dataProvider = sessionManager.dataProvider(token);
        UsersList users = dataProvider.users();
        return UserMessage.list(users);
    }

}

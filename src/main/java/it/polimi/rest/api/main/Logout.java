package it.polimi.rest.api.main;

import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.TokenHeaderExtractor;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.communication.messages.session.SessionMessage;
import it.polimi.rest.models.BasicToken;
import it.polimi.rest.models.TokenId;
import spark.Request;

import java.util.Optional;

/**
 * Remove a session.
 */
public class Logout extends Responder<TokenId, BasicToken.Id> {

    private final SessionManager sessionManager;

    public Logout(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected Optional<TokenExtractor<TokenId>> tokenExtractor() {
        return Optional.of(new TokenHeaderExtractor<>(TokenId::new));
    }

    @Override
    protected BasicToken.Id deserialize(Request request) {
        String id = request.params("tokenId");
        return new BasicToken.Id(id);
    }

    @Override
    protected Message process(TokenId token, BasicToken.Id data) {
        sessionManager.dataProvider(token).remove(data);
        sessionManager.remove(data);

        logger.d("Session " + data + " terminated");
        return SessionMessage.deletion();
    }

}

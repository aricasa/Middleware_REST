package it.polimi.rest.api.main;

import it.polimi.rest.authorization.AuthorizationProxy;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.TokenHeaderExtractor;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.communication.messages.session.SessionMessage;
import it.polimi.rest.models.BasicToken;
import it.polimi.rest.models.TokenId;
import spark.Request;

import java.util.Optional;

public class Logout extends Responder<TokenId, BasicToken.Id> {

    private final AuthorizationProxy proxy;

    public Logout(AuthorizationProxy proxy) {
        this.proxy = proxy;
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
        proxy.sessionsManager(token).remove(data);
        proxy.dataProvider(token).remove(data);

        logger.d("Session " + data + " terminated");
        return SessionMessage.deletion();
    }

}

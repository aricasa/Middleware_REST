package it.polimi.rest.api.main;

import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.TokenHeaderExtractor;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.communication.messages.RootMessage;
import it.polimi.rest.models.Root;
import it.polimi.rest.models.TokenId;
import spark.Request;

import java.util.Optional;

/**
 * Root page of the REST service.
 */
class RootPage extends Responder<TokenId, Void> {

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
        return new RootMessage(new Root());
    }

}

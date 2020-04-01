package it.polimi.rest.api.main;

import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.TokenHeaderExtractor;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.communication.messages.image.ImageMessage;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.models.ImagesList;
import it.polimi.rest.models.TokenId;
import spark.Request;

import java.util.Optional;

/**
 * Get the user images list (metadata only).
 */
public class UserImages extends Responder<TokenId, String> {

    private final SessionManager sessionManager;

    public UserImages(SessionManager sessionManager) {
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
    protected Message process(TokenId token, String data) {
        DataProvider dataProvider = sessionManager.dataProvider(token);
        ImagesList images = dataProvider.images(data);
        return ImageMessage.list(images);
    }

}

package it.polimi.rest.api.main;

import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.TokenHeaderExtractor;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.communication.messages.image.ImageMessage;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.models.Image;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.User;
import it.polimi.rest.utils.Pair;
import spark.Request;

import java.util.Optional;

/**
 * Remove an image.
 */
class ImageRemove extends Responder<TokenId, Pair<String, Image.Id>> {

    private final SessionManager sessionManager;

    public ImageRemove(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected Optional<TokenExtractor<TokenId>> tokenExtractor() {
        return Optional.of(new TokenHeaderExtractor<>(TokenId::new));
    }

    @Override
    protected Pair<String, Image.Id> deserialize(Request request) {
        String username = request.params("username");
        Image.Id imageId = new Image.Id(request.params("imageId"));

        return new Pair<>(username, imageId);
    }

    @Override
    protected Message process(TokenId token, Pair<String, Image.Id> data) {
        DataProvider dataProvider = sessionManager.dataProvider(token);
        dataProvider.remove(data.first, data.second);

        logger.d("Image " + data + " removed");
        return ImageMessage.deletion();
    }

}

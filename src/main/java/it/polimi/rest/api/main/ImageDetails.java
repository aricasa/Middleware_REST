package it.polimi.rest.api.main;

import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.TokenHeaderExtractor;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.communication.messages.image.ImageMessage;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.exceptions.NotFoundException;
import it.polimi.rest.models.Image;
import it.polimi.rest.models.TokenId;
import spark.Request;

import java.util.Optional;

/**
 * Get the metadata of an image.
 */
class ImageDetails extends Responder<TokenId, ImageDetails.Data> {

    private final SessionManager sessionManager;

    public ImageDetails(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected Optional<TokenExtractor<TokenId>> tokenExtractor() {
        return Optional.of(new TokenHeaderExtractor<>(TokenId::new));
    }

    @Override
    protected ImageDetails.Data deserialize(Request request) {
        String username = request.params("username");
        Image.Id imageId = new Image.Id(request.params("imageId"));

        return new ImageDetails.Data(username, imageId);
    }

    @Override
    protected Message process(TokenId token, ImageDetails.Data data) {
        DataProvider dataProvider = sessionManager.dataProvider(token);
        Image image = dataProvider.image(data.imageId);

        if (!image.info.owner.username.equals(data.username)) {
            throw new NotFoundException();
        }

        return ImageMessage.details(image.info);
    }

    protected static class Data {
        public final String username;
        public final Image.Id imageId;

        public Data(String username, Image.Id imageId) {
            this.username = username;
            this.imageId = imageId;
        }
    }

}

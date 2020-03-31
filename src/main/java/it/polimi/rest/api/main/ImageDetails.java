package it.polimi.rest.api.main;

import it.polimi.rest.authorization.AuthorizationProxy;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.TokenHeaderExtractor;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.communication.messages.image.ImageMessage;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.models.Image;
import it.polimi.rest.models.TokenId;
import spark.Request;

import java.util.Optional;

class ImageDetails extends Responder<TokenId, Image.Id> {

    private final AuthorizationProxy proxy;

    public ImageDetails(AuthorizationProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    protected Optional<TokenExtractor<TokenId>> tokenExtractor() {
        return Optional.of(new TokenHeaderExtractor<>(TokenId::new));
    }

    @Override
    protected Image.Id deserialize(Request request) {
        String id = request.params("imageId");
        return new Image.Id(id);
    }

    @Override
    protected Message process(TokenId token, Image.Id imageId) {
        DataProvider dataProvider = proxy.dataProvider(token);
        Image image = dataProvider.image(imageId);
        return ImageMessage.details(image.info);
    }

}

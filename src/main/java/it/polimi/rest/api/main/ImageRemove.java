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

class ImageRemove extends Responder<TokenId, Image.Id> {

    private final AuthorizationProxy proxy;

    public ImageRemove(AuthorizationProxy proxy) {
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
    protected Message process(TokenId token, Image.Id data) {
        DataProvider dataProvider = proxy.dataProvider(token);
        dataProvider.remove(data);

        logger.d("Image " + data + " removed");
        return ImageMessage.deletion();
    }

}

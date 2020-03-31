package it.polimi.rest.api.main;

import it.polimi.rest.authorization.Authorization;
import it.polimi.rest.authorization.AuthorizationProxy;
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

public class UserImages extends Responder<TokenId, String> {

    private final AuthorizationProxy proxy;

    public UserImages(AuthorizationProxy proxy) {
        this.proxy = proxy;
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
        DataProvider dataProvider = proxy.dataProvider(token);
        ImagesList images = dataProvider.images(data);
        return ImageMessage.list(images);
    }

}

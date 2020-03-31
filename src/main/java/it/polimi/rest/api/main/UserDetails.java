package it.polimi.rest.api.main;

import it.polimi.rest.authorization.AuthorizationProxy;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.communication.messages.user.UserMessage;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.User;
import spark.Request;

import java.util.Optional;

class UserDetails extends Responder<TokenId, String> {

    private final AuthorizationProxy proxy;

    public UserDetails(AuthorizationProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    protected Optional<TokenExtractor<TokenId>> tokenExtractor() {
        return Optional.empty();
    }

    @Override
    protected String deserialize(Request request) {
        return request.params("username");
    }

    @Override
    protected Message process(TokenId token, String username) {
        DataProvider dataProvider = proxy.dataProvider(token);
        User user = dataProvider.userByUsername(username);
        return UserMessage.details(user);
    }

}

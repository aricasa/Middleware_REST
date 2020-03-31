package it.polimi.rest.api.main;

import it.polimi.rest.adapters.GsonDeserializer;
import it.polimi.rest.authorization.AuthorizationProxy;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.communication.messages.user.UserMessage;
import it.polimi.rest.credentials.CredentialsManager;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.models.Id;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.User;
import it.polimi.rest.utils.Logger;
import spark.Request;

import java.util.Optional;

class UserAdd extends Responder<TokenId, User> {

    private final AuthorizationProxy proxy;
    private final CredentialsManager credentialsManager;

    public UserAdd(AuthorizationProxy proxy, CredentialsManager credentialsManager) {
        this.proxy = proxy;
        this.credentialsManager = credentialsManager;
    }

    @Override
    protected Optional<TokenExtractor<TokenId>> tokenExtractor() {
        return Optional.empty();
    }

    @Override
    protected User deserialize(Request request) {
        return new GsonDeserializer<>(User.class).parse(request);
    }

    @Override
    protected Message process(TokenId token, User data) {
        DataProvider dataProvider = proxy.dataProvider(token);

        User.Id userId = dataProvider.uniqueId(Id::randomizer, User.Id::new);
        User user = new User(userId, data.username, data.password);
        dataProvider.add(user);
        credentialsManager.add(user.id, user.username, user.password);

        logger.d("User " + user + " created");
        return UserMessage.creation(user);
    }

}

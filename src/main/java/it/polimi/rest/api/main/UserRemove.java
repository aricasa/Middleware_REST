package it.polimi.rest.api.main;

import it.polimi.rest.authorization.AuthorizationProxy;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.TokenHeaderExtractor;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.communication.messages.user.UserMessage;
import it.polimi.rest.credentials.CredentialsManager;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.User;
import spark.Request;

import java.util.Optional;

/**
 * Remove a user.
 */
class UserRemove extends Responder<TokenId, String> {

    private final AuthorizationProxy proxy;
    private final CredentialsManager credentialsManager;

    public UserRemove(AuthorizationProxy proxy, CredentialsManager credentialsManager) {
        this.proxy = proxy;
        this.credentialsManager = credentialsManager;
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
    protected Message process(TokenId token, String username) {
        DataProvider dataProvider = proxy.dataProvider(token);

        User user = dataProvider.userByUsername(username);
        dataProvider.remove(user.id);
        credentialsManager.remove(user.id);

        logger.d("User " + user + " removed");
        return UserMessage.deletion();
    }

}

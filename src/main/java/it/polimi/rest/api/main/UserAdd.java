package it.polimi.rest.api.main;

import it.polimi.rest.adapters.GsonDeserializer;
import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.communication.messages.user.UserMessage;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.exceptions.BadRequestException;
import it.polimi.rest.models.Id;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.User;
import spark.Request;

import java.util.Optional;

/**
 * Add a new user.
 */
class UserAdd extends Responder<TokenId, User> {

    private final SessionManager sessionManager;

    public UserAdd(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected Optional<TokenExtractor<TokenId>> tokenExtractor() {
        return Optional.empty();
    }

    @Override
    protected User deserialize(Request request) {
        User user = new GsonDeserializer<>(User.class).parse(request);

        if (user.username == null || user.username.trim().isEmpty()) {
            throw new BadRequestException("Username not specified");

        } else if (user.password == null || user.password.trim().isEmpty()) {
            throw new BadRequestException("Password not specified");
        }

        return user;
    }

    @Override
    protected Message process(TokenId token, User data) {
        DataProvider dataProvider = sessionManager.dataProvider(token);

        User.Id userId = dataProvider.uniqueId(Id::randomizer, User.Id::new);
        User user = new User(userId, data.username, data.password);
        dataProvider.add(user);

        logger.d("User " + user + " created");
        return UserMessage.creation(user);
    }

}

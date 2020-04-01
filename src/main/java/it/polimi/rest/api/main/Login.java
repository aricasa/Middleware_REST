package it.polimi.rest.api.main;

import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.communication.messages.session.SessionMessage;
import it.polimi.rest.authentication.Authenticator;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.BasicToken;
import it.polimi.rest.models.Id;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.User;
import spark.Request;

import java.util.Base64;
import java.util.Optional;

import static it.polimi.rest.exceptions.UnauthorizedException.AuthType.BASIC;

/**
 * Check the user credentials and, if valid, create a new session.
 */
public class Login extends Responder<TokenId, Login.Data> {

    /** Session lifetime (in seconds) */
    private static final int SESSION_LIFETIME = 60 * 60;

    private final SessionManager sessionManager;
    private final Authenticator credentialsManager;

    public Login(SessionManager sessionManager, Authenticator credentialsManager) {
        this.sessionManager = sessionManager;
        this.credentialsManager = credentialsManager;
    }

    @Override
    protected Optional<TokenExtractor<TokenId>> tokenExtractor() {
        return Optional.empty();
    }

    @Override
    protected Data deserialize(Request request) {
        return Optional.ofNullable(request.headers("Authorization"))
                .filter(header -> header.startsWith("Basic"))
                .map(header -> header.substring("Basic".length()).trim())
                .map(encoded -> new String(Base64.getDecoder().decode(encoded)))
                .map(decoded -> decoded.split(":"))
                .filter(credentials -> credentials.length == 2)
                .map(credentials -> new Data(credentials[0], credentials[1]))
                .orElseThrow(() -> new UnauthorizedException(BASIC));
    }

    @Override
    protected Message process(TokenId token, Data data) {
        User.Id user = credentialsManager.authenticate(data.username, data.password);

        DataProvider dataProvider = sessionManager.dataProvider(token);

        BasicToken session = new BasicToken(dataProvider.uniqueId(Id::randomizer, BasicToken.Id::new), SESSION_LIFETIME, user);
        dataProvider.add(session);

        logger.d("User " + user + " logged in with session " + session);
        return SessionMessage.creation(session);
    }

    protected static class Data {

        public final String username;
        public final String password;

        public Data(String username, String password) {
            this.username = username;
            this.password = password;
        }

    }

}

package it.polimi.rest.api;

import it.polimi.rest.adapters.*;
import it.polimi.rest.authorization.AuthorizationProxy;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.authorization.Token;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.TokenHeaderExtractor;
import it.polimi.rest.communication.messages.*;
import it.polimi.rest.communication.messages.image.ImageMessage;
import it.polimi.rest.communication.messages.session.SessionMessage;
import it.polimi.rest.communication.messages.user.UserMessage;
import it.polimi.rest.credentials.CredentialsManager;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.exceptions.*;
import it.polimi.rest.models.*;
import it.polimi.rest.sessions.SessionsManager;
import it.polimi.rest.utils.Logger;
import it.polimi.rest.utils.Pair;
import spark.Route;

import java.util.Base64;
import java.util.Optional;

import static it.polimi.rest.exceptions.UnauthorizedException.AuthType.BASIC;

public class MainApi {

    private final Logger logger = new Logger(this.getClass());

    private final CredentialsManager credentialsManager;
    private final AuthorizationProxy proxy;

    private final TokenExtractor tokenHeaderExtractor = new TokenHeaderExtractor();

    private static final int SESSION_LIFETIME = 60 * 60;

    /**
     * Constructor.
     *
     * @param authorizer            authorizer
     * @param credentialsManager    credentials manager
     * @param sessionsManager       sessions manager
     * @param dataProvider          data provider
     */
    public MainApi(Authorizer authorizer,
                   CredentialsManager credentialsManager,
                   SessionsManager sessionsManager,
                   DataProvider dataProvider) {

        this.credentialsManager = credentialsManager;
        this.proxy = new AuthorizationProxy(authorizer, sessionsManager, dataProvider);
    }

    public Route root() {
        Deserializer<Void> deserializer = request -> null;
        Responder.Action<Void> action = (data, token) -> new RootMessage(new Root());
        return new Responder<>(tokenHeaderExtractor, deserializer, action);
    }

    public Route users() {
        Deserializer<Void> deserializer = request -> null;

        Responder.Action<Void> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            UsersList users = dataProvider.users();
            return UserMessage.list(users);
        };

        return new Responder<>(tokenHeaderExtractor, deserializer, action);
    }

    public Route signup() {
        Deserializer<User> deserializer = new GsonDeserializer<>(User.class);

        Responder.Action<User> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);

            User.Id userId = dataProvider.uniqueId(Id::randomizer, User.Id::new);
            User user = new User(userId, data.username, data.password);
            dataProvider.add(user);
            credentialsManager.add(user.id, user.username, user.password);

            logger.d("User " + user + " signed up");
            return UserMessage.creation(user);
        };

        return new Responder<>(tokenHeaderExtractor, deserializer, action);
    }

    public Route login() {
        Deserializer<Pair<String, String>> deserializer = request -> {
            Optional<String> authenticationHeader = Optional.ofNullable(request.headers("Authorization"));

            if (!authenticationHeader.isPresent()) {
                throw new UnauthorizedException(BASIC);
            }

            String authorization = authenticationHeader.get();

            if (!authorization.startsWith("Basic")) {
                throw new UnauthorizedException(BASIC);
            }

            String encoded = authorization.substring("Basic".length()).trim();
            String decoded = new String(Base64.getDecoder().decode(encoded));
            String[] credentials = decoded.split(":");
            return new Pair<>(credentials[0], credentials[1]);
        };

        Responder.Action<Pair<String, String>> action = (data, token) -> {
            User.Id user = credentialsManager.authenticate(data.first, data.second);

            SessionsManager sessionsManager = proxy.sessionsManager(token);
            BearerToken session = new BearerToken(sessionsManager.getUniqueId(Id::randomizer), SESSION_LIFETIME, user);
            sessionsManager.add(session);

            logger.d("User " + user + " logged in with session " + session);
            return SessionMessage.creation(session);
        };

        return new Responder<>(tokenHeaderExtractor, deserializer, action);
    }

    public Route logout(String tokenIdParam) {
        Deserializer<TokenId> deserializer = request -> {
            String id = request.params(tokenIdParam);
            return new TokenId(id);
        };

        Responder.Action<TokenId> action = (data, token) -> {
            proxy.sessionsManager(token).remove(data);
            logger.d("Session " + data + " terminated");

            return SessionMessage.deletion();
        };

        return new Responder<>(tokenHeaderExtractor, deserializer, action);
    }

    public Route userByUsername(String usernameParam) {
        Deserializer<String> deserializer = request -> request.params(usernameParam);

        Responder.Action<String> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            User user = dataProvider.userByUsername(data);
            return UserMessage.details(user);
        };

        return new Responder<>(tokenHeaderExtractor, deserializer, action);
    }

    public Route userById(String idParam) {
        Deserializer<User.Id> deserializer = request -> {
            String id = request.params(idParam);
            return new User.Id(id);
        };

        Responder.Action<User.Id> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            User user = dataProvider.userById(data);
            return UserMessage.details(user);
        };

        return new Responder<>(tokenHeaderExtractor, deserializer, action);
    }

    public Route removeUser(String usernameParam) {
        Deserializer<String> deserializer = request -> request.params(usernameParam);

        Responder.Action<String> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);

            User user = dataProvider.userByUsername(data);
            dataProvider.remove(user.id);
            credentialsManager.remove(user.id);

            logger.d("User " + user + " removed");
            return UserMessage.deletion();
        };

        return new Responder<>(tokenHeaderExtractor, deserializer, action);
    }

    public Route images(String usernameParam) {
        Deserializer<String> deserializer = request -> request.params(usernameParam);

        Responder.Action<String> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            User user = dataProvider.userByUsername(data);
            ImagesList images = dataProvider.images(user.id);
            return ImageMessage.list(images);
        };

        return new Responder<>(tokenHeaderExtractor, deserializer, action);
    }

    public Route imageDetails(String imageIdParam) {
        Deserializer<Image.Id> deserializer = request -> {
            String id = request.params(imageIdParam);
            return new Image.Id(id);
        };

        Responder.Action<Image.Id> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            Image image = dataProvider.image(data);
            return ImageMessage.details(image.info);
        };

        return new Responder<>(tokenHeaderExtractor, deserializer, action);
    }

    public Route imageRaw(String imageIdParam) {
        Deserializer<Image.Id> deserializer = request -> {
            String id = request.params(imageIdParam);
            return new Image.Id(id);
        };

        Responder.Action<Image.Id> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            Image image = dataProvider.image(data);
            return ImageMessage.raw(image);
        };

        return new Responder<>(tokenHeaderExtractor, deserializer, action);
    }

    public Route addImage(String usernameParam) {
        Deserializer<Image> deserializer = new ImageDeserializer(usernameParam);

        Responder.Action<Image> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);

            User user = dataProvider.userByUsername(data.info.owner.username);

            ImageMetadata metadata = new ImageMetadata(
                    dataProvider.uniqueId(Id::randomizer, Image.Id::new),
                    data.info.title,
                    user
            );

            Image image = new Image(metadata, data.data);
            dataProvider.add(image);

            logger.d("Image " + data.info.id + " added");
            return ImageMessage.creation(data.info);
        };

        return new Responder<>(tokenHeaderExtractor, deserializer, action);
    }

    public Route removeImage(String imageIdParam) {
        Deserializer<Image.Id> deserializer = request -> {
            String id = request.params(imageIdParam);
            return new Image.Id(id);
        };

        Responder.Action<Image.Id> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            dataProvider.remove(data);

            logger.d("Image " + data + " removed");
            return ImageMessage.deletion();
        };

        return new Responder<>(tokenHeaderExtractor, deserializer, action);
    }

}

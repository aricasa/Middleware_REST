package it.polimi.rest;

import it.polimi.rest.adapters.GsonDeserializer;
import it.polimi.rest.adapters.Deserializer;
import it.polimi.rest.adapters.ImageDeserializer;
import it.polimi.rest.authorization.AuthorizationProxy;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.authorization.Token;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.messages.*;
import it.polimi.rest.credentials.CredentialsManager;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.*;
import it.polimi.rest.sessions.SessionsManager;
import it.polimi.rest.utils.Logger;
import it.polimi.rest.utils.Pair;
import spark.Request;
import spark.Route;

import java.util.Base64;
import java.util.Optional;

import static it.polimi.rest.exceptions.UnauthorizedException.AuthType.BASIC;

public class ImageServerAPI {

    private final Logger logger = new Logger(this.getClass());

    private final CredentialsManager credentialsManager;
    private final AuthorizationProxy proxy;

    private static final int SESSION_LIFETIME = 60 * 60;

    /**
     * Constructor.
     *
     * @param authorizer            authorizer
     * @param credentialsManager    credentials manager
     * @param sessionsManager       sessions manager
     * @param dataProvider          data provider
     */
    public ImageServerAPI(Authorizer authorizer,
                          CredentialsManager credentialsManager,
                          SessionsManager sessionsManager,
                          DataProvider dataProvider) {

        this.credentialsManager = credentialsManager;
        this.proxy = new AuthorizationProxy(authorizer, sessionsManager, dataProvider);
    }

    public Route root() {
        Deserializer<Void> deserializer = (request, token) -> null;
        Responder.Action<Void> action = (data, token) -> new RootMessage(new Root());
        return new Responder<>(deserializer, action);
    }

    public Route users() {
        Deserializer<Void> deserializer = (request, token) -> null;

        Responder.Action<Void> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            UsersList users = dataProvider.users();
            return new UsersListMessage(users);
        };

        return new Responder<>(deserializer, action);
    }

    public Route signup() {
        Deserializer<User> deserializer = new GsonDeserializer<>(User.class);

        Responder.Action<User> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);

            User user = new User(dataProvider.uniqueId(UserId::new), data.username, data.password);
            dataProvider.add(user);
            credentialsManager.add(user.id, user.username, user.password);

            logger.d("User " + user + " signed up");
            return new UserCreationMessage(user);
        };

        return new Responder<>(deserializer, action);
    }

    public Route login() {
        Deserializer<Pair<String, String>> deserializer = (request, token) -> {
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
            UserId user = credentialsManager.authenticate(data.first, data.second);

            SessionsManager sessionsManager = proxy.sessionManager(token);
            BearerToken session = new BearerToken(sessionsManager.getUniqueId(), SESSION_LIFETIME, user);
            sessionsManager.add(session);

            logger.d("User " + session.user + " logged in with session " + session);
            return new LoginMessage(session);
        };

        return new Responder<>(deserializer, action);
    }

    public Route logout(String tokenIdParam) {
        Deserializer<TokenId> deserializer = (request, token) -> {
            String id = request.params(tokenIdParam);
            return new TokenId(id);
        };

        Responder.Action<TokenId> action = (data, token) -> {
            SessionsManager sessionsManager = proxy.sessionManager(token);

            Token t = sessionsManager.token(data);
            sessionsManager.remove(t.id());
            logger.d("Session " + t + " terminated");

            return new LogoutMessage();
        };

        return new Responder<>(deserializer, action);
    }

    public Route userByUsername(String usernameParam) {
        Deserializer<String> deserializer = (request, token) -> request.params(usernameParam);

        Responder.Action<String> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            User user = dataProvider.userByUsername(data);
            return new UserDetailsMessage(user);
        };

        return new Responder<>(deserializer, action);
    }

    public Route userById(String idParam) {
        Deserializer<UserId> deserializer = (request, token) -> {
            String id = request.params(idParam);
            return new UserId(id);
        };

        Responder.Action<UserId> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            User user = dataProvider.userById(data);
            return new UserDetailsMessage(user);
        };

        return new Responder<>(deserializer, action);
    }

    public Route removeUser(String usernameParam) {
        Deserializer<String> deserializer = (request, token) -> request.params(usernameParam);

        Responder.Action<String> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);

            User user = dataProvider.userByUsername(data);
            dataProvider.remove(user.id);
            credentialsManager.remove(user.id);

            logger.d("User " + user + " removed");
            return new UserDeletionMessage();
        };

        return new Responder<>(deserializer, action);
    }

    public Route userImages(String usernameParam) {
        Deserializer<String> deserializer = (request, token) -> request.params(usernameParam);

        Responder.Action<String> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            User user = dataProvider.userByUsername(data);
            ImagesList images = dataProvider.images(user.id);
            return new ImagesListMessage(images);
        };

        return new Responder<>(deserializer, action);
    }

    public Route imageDetails(String imageIdParam) {
        Deserializer<ImageId> deserializer = (request, token) -> {
            String id = request.params(imageIdParam);
            return new ImageId(id);
        };

        Responder.Action<ImageId> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            Image image = dataProvider.image(data);
            return new ImageDetailsMessage(image.info);
        };

        return new Responder<>(deserializer, action);
    }

    public Route imageRaw(String imageIdParam) {
        Deserializer<ImageId> deserializer = (request, token) -> {
            String id = request.params(imageIdParam);
            return new ImageId(id);
        };

        Responder.Action<ImageId> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            Image image = dataProvider.image(data);
            return new ImageMessage(image);
        };

        return new Responder<>(deserializer, action);
    }

    public Route addImage(String usernameParam) {
        Deserializer<Image> deserializer = (request, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            ImageDeserializer imageDeserializer = new ImageDeserializer(usernameParam, dataProvider);
            return imageDeserializer.parse(request, token);
        };

        Responder.Action<Image> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            dataProvider.add(data);

            logger.d("Image " + data.info.id + " added");
            return new ImageCreationMessage(data.info);
        };

        return new Responder<>(deserializer, action);
    }

    public Route removeImage(String imageIdParam) {
        Deserializer<ImageId> deserializer = (request, token) -> {
            String id = request.params(imageIdParam);
            return new ImageId(id);
        };

        Responder.Action<ImageId> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            dataProvider.remove(data);

            logger.d("Image " + data + " removed");
            return new ImageDeletionMessage();
        };

        return new Responder<>(deserializer, action);
    }

    public Route registerClient(String usernameParam) {
        Deserializer<Pair<String, OAuthClient>> deserializer = new Deserializer<Pair<String, OAuthClient>>() {
            @Override
            public Pair<String, OAuthClient> parse(Request request, TokenId token) {
                String username = request.params(usernameParam);
                OAuthClient client = new GsonDeserializer<>(OAuthClient.class).parse(request, token);
                return new Pair<>(username, client);
            }
        };

        Responder.Action<Pair<String, OAuthClient>> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            User user = dataProvider.userByUsername(data.first);

            OAuthClient oAuthClient = new OAuthClient(user.id,
                    dataProvider.uniqueId(OAuthClientId::new),
                dataProvider.uniqueId(OAuthClientSecret::new),
                data.second.name,
                data.second.callback);

            dataProvider.add(oAuthClient);
            return new OAuthClientCreationMessage(null);
        };

        return new Responder<>(deserializer, action);
    }

}

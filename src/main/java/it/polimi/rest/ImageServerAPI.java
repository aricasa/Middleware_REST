package it.polimi.rest;

import it.polimi.rest.adapters.GsonDeserializer;
import it.polimi.rest.adapters.Deserializer;
import it.polimi.rest.adapters.ImageDeserializer;
import it.polimi.rest.authorization.AuthorizationProxy;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.messages.*;
import it.polimi.rest.credentials.CredentialsManager;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.models.*;
import it.polimi.rest.sessions.SessionsManager;
import spark.Route;

public class ImageServerAPI {

    private final Logger logger = new Logger(this.getClass());

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

        this.proxy = new AuthorizationProxy(authorizer, credentialsManager, sessionsManager, dataProvider);
    }

    public Route root() {
        Deserializer<Void> deserializer = (request, token) -> null;
        Responder.Action<Void> action = (data, token) -> new RootMessage(new Root());
        return new Responder<>(deserializer, action);
    }

    public Route users() {
        Deserializer<Void> deserializer = (request, token) -> null;

        Responder.Action<Void> action = (data, token) -> {
            CredentialsManager credentialsManager = proxy.getCredentialsManager(token);
            UsersList users = credentialsManager.users();
            return new UsersListMessage(users);
        };

        return new Responder<>(deserializer, action);
    }

    public Route signup() {
        Deserializer<User> deserializer = new GsonDeserializer<>(User.class);

        Responder.Action<User> action = (data, token) -> {
            CredentialsManager credentialsManager = proxy.getCredentialsManager(token);
            User user = new User(credentialsManager.getUniqueId(), data.username, data.password);
            credentialsManager.add(user);
            logger.d("User " + user + " created");
            return new UserCreationMessage(user);
        };

        return new Responder<>(deserializer, action);
    }

    public Route login() {
        Deserializer<User> deserializer = new GsonDeserializer<>(User.class);

        Responder.Action<User> action = (data, token) -> {
            CredentialsManager credentialsManager = proxy.getCredentialsManager(token);
            SessionsManager sessionsManager = proxy.getSessionsManager(token);
            User user = credentialsManager.authenticate(data.username, data.password);
            Token session = new Token(sessionsManager.getUniqueId(), SESSION_LIFETIME, user.id, user.id);
            sessionsManager.add(session);
            logger.d("User " + session.owner + " logged in");
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
            SessionsManager sessionsManager = proxy.getSessionsManager(token);
            Token t = sessionsManager.token(data);
            sessionsManager.remove(t.id);
            return new LogoutMessage();
        };

        return new Responder<>(deserializer, action);
    }

    public Route userByUsername(String usernameParam) {
        Deserializer<String> deserializer = (request, token) -> request.params(usernameParam);

        Responder.Action<String> action = (data, token) -> {
            CredentialsManager credentialsManager = proxy.getCredentialsManager(token);
            User user = credentialsManager.userByUsername(data);
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
            CredentialsManager credentialsManager = proxy.getCredentialsManager(token);
            User user = credentialsManager.userById(data);
            return new UserDetailsMessage(user);
        };

        return new Responder<>(deserializer, action);
    }

    public Route deleteUser(String usernameParam) {
        Deserializer<String> deserializer = (request, token) -> request.params(usernameParam);

        Responder.Action<String> action = (data, token) -> {
            CredentialsManager credentialsManager = proxy.getCredentialsManager(token);
            User user = credentialsManager.userByUsername(data);
            credentialsManager.remove(user.id);
            return new UserDeletionMessage();
        };

        return new Responder<>(deserializer, action);
    }

    public Route userImages(String usernameParam) {
        Deserializer<String> deserializer = (request, token) -> request.params(usernameParam);

        Responder.Action<String> action = (data, token) -> {
            CredentialsManager credentialsManager = proxy.getCredentialsManager(token);
            DataProvider dataProvider = proxy.getDataProvider(token);
            User user = credentialsManager.userByUsername(data);
            ImagesList images = dataProvider.images(user.id);
            return new ImagesListMessage(images);
        };

        return new Responder<>(deserializer, action);
    }

    public Route getImageDetails(String imageIdParam) {
        Deserializer<ImageId> deserializer = (request, token) -> {
            String id = request.params(imageIdParam);
            return new ImageId(id);
        };

        Responder.Action<ImageId> action = (data, token) -> {
            DataProvider dataProvider = proxy.getDataProvider(token);
            Image image = dataProvider.image(data);
            return new ImageDetailsMessage(image.info);
        };

        return new Responder<>(deserializer, action);
    }

    public Route getImageRaw(String imageIdParam) {
        Deserializer<ImageId> deserializer = (request, token) -> {
            String id = request.params(imageIdParam);
            return new ImageId(id);
        };

        Responder.Action<ImageId> action = (data, token) -> {
            DataProvider dataProvider = proxy.getDataProvider(token);
            Image image = dataProvider.image(data);
            return new ImageMessage(image);
        };
        return new Responder<>(deserializer, action);
    }

    public Route newImage(String usernameParam) {
        Deserializer<Image> deserializer = (request, token) -> {
            CredentialsManager credentialsManager = proxy.getCredentialsManager(token);
            DataProvider dataProvider = proxy.getDataProvider(token);
            ImageDeserializer imageDeserializer = new ImageDeserializer(usernameParam, credentialsManager, dataProvider);
            return imageDeserializer.parse(request, token);
        };

        Responder.Action<Image> action = (data, token) -> {
            DataProvider dataProvider = proxy.getDataProvider(token);
            dataProvider.add(data);
            return new ImageCreationMessage(data.info);
        };

        return new Responder<>(deserializer, action);
    }

    public Route deleteImage(String imageIdParam) {
        Deserializer<ImageId> deserializer = (request, token) -> {
            String id = request.params(imageIdParam);
            return new ImageId(id);
        };

        Responder.Action<ImageId> action = (data, token) -> {
            DataProvider dataProvider = proxy.getDataProvider(token);
            dataProvider.remove(data);
            return new ImageDeletionMessage();
        };

        return new Responder<>(deserializer, action);
    }

}

package it.polimi.rest;

import it.polimi.rest.adapters.*;
import it.polimi.rest.authorization.Agent;
import it.polimi.rest.authorization.AuthorizationProxy;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.authorization.Token;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.messages.*;
import it.polimi.rest.communication.messages.oauth2.*;
import it.polimi.rest.credentials.CredentialsManager;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.exceptions.BadRequestException;
import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.*;
import it.polimi.rest.models.oauth2.*;
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

    private TokenExtractor bearerAuthentication = request -> {
        String authorization = request.headers("Authorization");

        if (authorization == null) {
            return null;
        }

        if (!authorization.startsWith("Bearer")) {
            return null;
        }

        return new TokenId(authorization.substring("Bearer".length()).trim());
    };

    public Route root() {
        Deserializer<Void> deserializer = request -> null;
        Responder.Action<Void> action = (data, token) -> new RootMessage(new Root());
        return new Responder<>(bearerAuthentication, deserializer, action);
    }

    public Route users() {
        Deserializer<Void> deserializer = request -> null;

        Responder.Action<Void> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            UsersList users = dataProvider.users();
            return new UsersListMessage(users);
        };

        return new Responder<>(bearerAuthentication, deserializer, action);
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
            return new UserCreationMessage(user);
        };

        return new Responder<>(bearerAuthentication, deserializer, action);
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
            return new LoginMessage(session);
        };

        return new Responder<>(bearerAuthentication, deserializer, action);
    }

    public Route logout(String tokenIdParam) {
        Deserializer<TokenId> deserializer = request -> {
            String id = request.params(tokenIdParam);
            return new TokenId(id);
        };

        Responder.Action<TokenId> action = (data, token) -> {
            SessionsManager sessionsManager = proxy.sessionsManager(token);

            Token t = sessionsManager.token(data);
            sessionsManager.remove(t.id());
            logger.d("Session " + t + " terminated");

            return new LogoutMessage();
        };

        return new Responder<>(bearerAuthentication, deserializer, action);
    }

    public Route userByUsername(String usernameParam) {
        Deserializer<String> deserializer = request -> request.params(usernameParam);

        Responder.Action<String> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            User user = dataProvider.userByUsername(data);
            return new UserDetailsMessage(user);
        };

        return new Responder<>(bearerAuthentication, deserializer, action);
    }

    public Route userById(String idParam) {
        Deserializer<User.Id> deserializer = request -> {
            String id = request.params(idParam);
            return new User.Id(id);
        };

        Responder.Action<User.Id> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            User user = dataProvider.userById(data);
            return new UserDetailsMessage(user);
        };

        return new Responder<>(bearerAuthentication, deserializer, action);
    }

    public Route removeUser(String usernameParam) {
        Deserializer<String> deserializer = request -> request.params(usernameParam);

        Responder.Action<String> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);

            User user = dataProvider.userByUsername(data);
            dataProvider.remove(user.id);
            credentialsManager.remove(user.id);

            logger.d("User " + user + " removed");
            return new UserDeletionMessage();
        };

        return new Responder<>(bearerAuthentication, deserializer, action);
    }

    public Route images(String usernameParam) {
        Deserializer<String> deserializer = request -> request.params(usernameParam);

        Responder.Action<String> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            User user = dataProvider.userByUsername(data);
            ImagesList images = dataProvider.images(user.id);
            return new ImagesListMessage(images);
        };

        return new Responder<>(bearerAuthentication, deserializer, action);
    }

    public Route imageDetails(String imageIdParam) {
        Deserializer<Image.Id> deserializer = request -> {
            String id = request.params(imageIdParam);
            return new Image.Id(id);
        };

        Responder.Action<Image.Id> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            Image image = dataProvider.image(data);
            return new ImageDetailsMessage(image.info);
        };

        return new Responder<>(bearerAuthentication, deserializer, action);
    }

    public Route imageRaw(String imageIdParam) {
        Deserializer<Image.Id> deserializer = request -> {
            String id = request.params(imageIdParam);
            return new Image.Id(id);
        };

        Responder.Action<Image.Id> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            Image image = dataProvider.image(data);
            return new ImageMessage(image);
        };

        return new Responder<>(bearerAuthentication, deserializer, action);
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
            return new ImageCreationMessage(data.info);
        };

        return new Responder<>(bearerAuthentication, deserializer, action);
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
            return new ImageDeletionMessage();
        };

        return new Responder<>(bearerAuthentication, deserializer, action);
    }

    public Route oAuth2Clients(String usernameParam) {
        Deserializer<String> deserializer = request -> request.params(usernameParam);

        Responder.Action<String> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            User user = dataProvider.userByUsername(data);
            OAuth2ClientsList clients = dataProvider.oAuth2Clients(user.id);
            return new OAuth2ClientsListMessage(clients);
        };

        return new Responder<>(bearerAuthentication, deserializer, action);
    }

    public Route addOAuth2Client(String usernameParam) {
        Deserializer<Pair<String, OAuth2Client>> deserializer = request -> {
            OAuth2Client client = new GsonDeserializer<>(OAuth2Client.class).parse(request);
            String username = request.params(usernameParam);
            return new Pair<>(username, client);
        };

        Responder.Action<Pair<String, OAuth2Client>> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            User user = dataProvider.userByUsername(data.first);

            OAuth2Client.Id id = dataProvider.uniqueId(Id::randomizer, OAuth2Client.Id::new);
            OAuth2Client.Secret secret = dataProvider.uniqueId(Id::randomizer, OAuth2Client.Secret::new);

            OAuth2Client oAuthClient = new OAuth2Client(user, id, secret, data.second.name, data.second.callback);
            dataProvider.add(oAuthClient);

            logger.d("OAuth2 client " + oAuthClient + " added");
            return new OAuth2ClientCreationMessage(oAuthClient);
        };

        return new Responder<>(bearerAuthentication, deserializer, action);
    }

    public Route removeOAuth2Client(String clientIdParam) {
        Deserializer<OAuth2Client.Id> deserializer = request -> {
            String id = request.params(clientIdParam);
            return new OAuth2Client.Id(id);
        };

        Responder.Action<OAuth2Client.Id> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            dataProvider.remove(data);
            return new OAuth2ClientDeletionMessage();
        };

        return new Responder<>(bearerAuthentication, deserializer, action);
    }

    public Route oAuth2Authorize() {
        Deserializer<OAuth2AuthorizationRequest> deserializer = new OAuth2AuthorizationRequestDeserializer();
        Responder.Action<OAuth2AuthorizationRequest> action = (data, token) -> new OAuth2LoginPage(data);

        return new Responder<>(bearerAuthentication, deserializer, action);
    }

    public Route oAuth2GrantPermissions() {
        Deserializer<OAuth2AuthorizationRequest> deserializer = new GsonDeserializer<>(OAuth2AuthorizationRequest.class);

        Responder.Action<OAuth2AuthorizationRequest> action = (data, token) -> {
            OAuth2Client client = proxy.dataProvider(new Token() {
                @Override
                public TokenId id() {
                    return null;
                }

                @Override
                public Agent agent() {
                    return data.client;
                }

                @Override
                public boolean isValid() {
                    return true;
                }
            }).oAuth2Client(data.client);

            if (!client.callback.equals(data.callback)) {
                throw new BadRequestException();
            }

            DataProvider dataProvider = proxy.dataProvider(token);

            OAuth2AuthorizationCode code = new OAuth2AuthorizationCode(
                    dataProvider.uniqueId(Id::randomizer, OAuth2AuthorizationCode.Id::new),
                    client.id, data.scopes);

            dataProvider.add(code);
            return new OAuth2AuthorizationCodeCreationMessage(code);
        };

        return new Responder<>(bearerAuthentication, deserializer, action);
    }

    public Route oAuth2Token() {
        Deserializer<String> deserializer = new Deserializer<String>() {
            @Override
            public String parse(Request request) {
                return null;
            }
        };

        Responder.Action<String> action = new Responder.Action<String>() {
            @Override
            public Message run(String data, TokenId token) {
                return null;
            }
        };

        return new Responder<>(bearerAuthentication, deserializer, action);
    }

}

package it.polimi.rest;

import it.polimi.rest.adapters.GsonDeserializer;
import it.polimi.rest.adapters.Deserializer;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.messages.*;
import it.polimi.rest.credentials.CredentialsManager;
import it.polimi.rest.exceptions.*;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.models.*;
import it.polimi.rest.sessions.SessionsManager;
import org.apache.commons.io.IOUtils;
import spark.Route;

import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

import static java.util.UUID.randomUUID;

public class ImageServerAPI {

    private final Logger logger = new Logger(this.getClass());

    private final CredentialsManager credentialsManager;
    private final SessionsManager sessionsManager;
    private final DataProvider dataProvider;

    private static final int SESSION_LIFETIME = 60 * 60;

    /**
     * Constructor.
     *
     * @param credentialsManager    credentials manager
     * @param sessionsManager       sessions manager
     * @param dataProvider          data provider
     */
    public ImageServerAPI(CredentialsManager credentialsManager, SessionsManager sessionsManager, DataProvider dataProvider) {
        this.credentialsManager = credentialsManager;
        this.sessionsManager = sessionsManager;
        this.dataProvider = dataProvider;
    }

    public Route root() {
        Deserializer<Void> deserializer = request -> null;
        Responder.Action<Void> action = (payload, token) -> new RootMessage(new Root());
        return new Responder<>(sessionsManager, deserializer, action);
    }

    public Route users() {
        Deserializer<Void> deserializer = request -> null;

        Responder.Action<Void> action = (payload, token) -> {
            Collection<User> users = credentialsManager.users();
            return new UsersListMessage(new UsersList(users));
        };

        return new Responder<>(sessionsManager, deserializer, action);
    }

    public Route signup() {
        Deserializer<User> deserializer = new GsonDeserializer<>(User.class);

        Responder.Action<User> action = (payload, token) -> {
            credentialsManager.userByUsername(payload.username).ifPresent(user -> {
                logger.w("Signup: " + user.username + " already in use");
                throw new ForbiddenException("Username already in use");
            });

            User user = new User(credentialsManager.getUniqueId(), payload.username, payload.password);
            credentialsManager.add(user);
            logger.d("User " + user + " created");
            return new UserCreationMessage(user);
        };

        return new Responder<>(sessionsManager, deserializer, action);
    }

    public Route login() {
        Deserializer<User> deserializer = new GsonDeserializer<>(User.class);

        Responder.Action<User> action = (payload, token) -> {
            Optional<User> user = credentialsManager.authenticate(payload.username, payload.password);

            if (!user.isPresent()) {
                throw new UnauthorizedException();
            }

            Token session = new Token(sessionsManager.getUniqueId(), SESSION_LIFETIME, user.get().id, user.get().id);
            sessionsManager.add(session);
            logger.d("User " + session.owner + " logged in");
            return new LoginMessage(session);
        };

        return new Responder<>(sessionsManager, deserializer, action);
    }

    public Route logout(String tokenIdParam) {
        Deserializer<TokenId> deserializer = request -> {
            String id = request.params(tokenIdParam);
            return new TokenId(id);
        };

        Responder.Action<TokenId> action = (payload, token) -> {
            if (!payload.accept(token)) {
                throw new ForbiddenException();
            }

            sessionsManager.remove(payload);
            return new LogoutMessage();
        };

        return new Responder<>(sessionsManager, deserializer, action);
    }

    public Route userByUsername(String usernameParam) {
        Deserializer<String> deserializer = request -> request.params(usernameParam);

        Responder.Action<String> action = (payload, token) -> {
            Optional<User> user = credentialsManager.userByUsername(payload);

            if (!user.isPresent()) {
                throw new NotFoundException();
            } else if (!token.hasAccess(user.get())) {
                throw new ForbiddenException();
            }

            return new UserDetailsMessage(user.get());
        };

        return new Responder<>(sessionsManager, deserializer, action);
    }

    public Route userById(String idParam) {
        Deserializer<UserId> deserializer = request -> {
            String id = request.params(idParam);
            return new UserId(id);
        };

        Responder.Action<UserId> action = (payload, token) -> {
            Optional<User> user = credentialsManager.userById(payload);

            if (!user.isPresent()) {
                throw new NotFoundException();
            } else if (!token.hasAccess(user.get())) {
                throw new ForbiddenException();
            }

            return new UserDetailsMessage(user.get());
        };

        return new Responder<>(sessionsManager, deserializer, action);
    }

    public Route deleteUser(String usernameParam) {
        Deserializer<String> deserializer = request -> request.params(usernameParam);

        Responder.Action<String> action = (payload, token) -> {
            Optional<User> user = credentialsManager.userByUsername(payload);

            if (!user.isPresent()) {
                throw new NotFoundException();
            } else if (!token.hasAccess(user.get())) {
                throw new ForbiddenException();
            }

            credentialsManager.remove(user.get().id);
            return new UserDeletionMessage();
        };

        return new Responder<>(sessionsManager, deserializer, action);
    }

    public Route userImages(String usernameParam) {
        Deserializer<String> deserializer = request -> request.params(usernameParam);

        Responder.Action<String> action = (payload, token) -> {
            Optional<User> user = credentialsManager.userByUsername(payload);

            if (!user.isPresent()) {
                throw new NotFoundException();
            } else if (!token.hasAccess(user.get())) {
                throw new ForbiddenException();
            }

            Collection<ImageMetadata> images = dataProvider.get(user.get());
            return new ImagesListMessage(new ImagesList(user.get(), images));
        };

        return new Responder<>(sessionsManager, deserializer, action);
    }

    public Route getImageDetails(String imageIdParam) {
        Deserializer<ImageId> deserializer = request -> {
            String id = request.params(imageIdParam);
            return new ImageId(id);
        };

        Responder.Action<ImageId> action = (payload, token) -> {
            Optional<Image> image = dataProvider.get(payload);

            if (!image.isPresent()) {
                throw new NotFoundException();
            } else if (!token.hasAccess(image.get())) {
                throw new ForbiddenException();
            }

            return new ImageDetailsMessage(image.get().info);
        };

        return new Responder<>(sessionsManager, deserializer, action);
    }

    public Route getImageRaw(String imageIdParam) {
        Deserializer<ImageId> deserializer = request -> {
            String id = request.params(imageIdParam);
            return new ImageId(id);
        };

        Responder.Action<ImageId> action = (payload, token) -> {
            Optional<Image> image = dataProvider.get(payload);

            if (!image.isPresent()) {
                throw new NotFoundException();
            } else if (!token.hasAccess(image.get())) {
                throw new ForbiddenException();
            }

            return new ImageMessage(image.get());
        };

        return new Responder<>(sessionsManager, deserializer, action);
    }

    public Route newImage(String usernameParam) {
        Deserializer<Image> deserializer = request -> {
            String username = request.params(usernameParam);
            Optional<User> user = credentialsManager.userByUsername(username);

            if (!user.isPresent()) {
                throw new NotFoundException();
            }

            ImageId id;

            do {
                id = new ImageId(randomUUID().toString());
            } while (dataProvider.contains(id));

            try {
                Part titlePart = request.raw().getPart("title");
                String title = IOUtils.toString(titlePart.getInputStream(), Charset.defaultCharset());

                Part filePart = request.raw().getPart("file");
                InputStream stream = filePart.getInputStream();

                ImageMetadata metadata = new ImageMetadata(id, title, user.get());
                return new Image(metadata, IOUtils.toByteArray(stream));

            } catch (IOException | ServletException e) {
                throw new BadRequestException();
            }
        };

        Responder.Action<Image> action = (payload, token) -> {
            if (!token.hasAccess(payload.info.owner)) {
                throw new ForbiddenException();
            }

            dataProvider.put(payload);
            return new ImageCreationMessage(payload.info);
        };

        return new Responder<>(sessionsManager, deserializer, action);
    }

    public Route deleteImage(String imageIdParam) {
        Deserializer<ImageMetadata> deserializer = request -> {
            String id = request.params(imageIdParam);
            Optional<Image> image = dataProvider.get(new ImageId(id));

            if (!image.isPresent()) {
                throw new NotFoundException();
            }

            return image.get().info;
        };

        Responder.Action<ImageMetadata> action = (payload, token) -> {
            if (!token.hasAccess(payload)) {
                throw new ForbiddenException();
            }

            dataProvider.remove(payload.id);
            return new ImageDeletionMessage();
        };

        return new Responder<>(sessionsManager, deserializer, action);
    }

}

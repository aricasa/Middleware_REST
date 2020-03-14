package it.polimi.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.exceptions.*;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.models.*;
import it.polimi.rest.credentials.CredentialsManager;
import it.polimi.rest.messages.*;
import org.apache.commons.io.IOUtils;
import spark.Request;
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

    private final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private final CredentialsManager credentialsManager;
    private final Authorizer authorizer;
    private final DataProvider dataProvider;

    /**
     * Constructor.
     *
     * @param credentialsManager    credentials manager
     */
    public ImageServerAPI(CredentialsManager credentialsManager, Authorizer authorizer, DataProvider dataProvider) {
        this.credentialsManager = credentialsManager;
        this.authorizer = authorizer;
        this.dataProvider = dataProvider;
    }

    public Route root() {
        return Responder.build(request -> new RootMessage(new Root()));
    }

    public Route users() {
        return Responder.build(request -> {
            authenticate(request);
            Collection<User> users = credentialsManager.users();
            return new UsersListMessage(new UsersList(users));
        });
    }

    public Route signup() {
        return Responder.build(request -> {
            User user = gson.fromJson(request.body(), User.class);
            User registered = credentialsManager.signup(user);
            logger.d("User " + registered + " created");
            return new UserCreationMessage(registered);
        });
    }

    public Route login() {
        return Responder.build(request -> {
            User user = gson.fromJson(request.body(), User.class);
            Token token = credentialsManager.login(user);
            logger.d("User " + token.owner + " logged in");
            return new LoginMessage(token);
        });
    }

    public Route logout() {
        return Responder.build(request -> {
            User user = authenticate(request);
            authorizer.revoke(user);
            logger.d("User " + user + " logged out");
            return new LogoutMessage();
        });
    }

    public Route username(String usernameParam) {
        return Responder.build(request -> {
            authenticate(request);
            String username = request.params(usernameParam);
            User user = credentialsManager.userByUsername(username);
            return new UserDetailsMessage(user);
        });
    }

    public Route userId(String idParam) {
        return Responder.build(request -> {
            authenticate(request);
            String id = request.params(idParam);
            User user = credentialsManager.userById(id);
            return new UserDetailsMessage(user);
        });
    }

    public Route deleteUser(String usernameParam) {
        return Responder.build(request -> {
            User logged = authenticate(request);
            String username = request.params(usernameParam);
            User userToBeRemoved = credentialsManager.userByUsername(username);

            if (!logged.equals(userToBeRemoved)) {
                throw new ForbiddenException();
            }

            credentialsManager.delete(userToBeRemoved);
            return new UserDeletionMessage();
        });
    }

    public Route userImages(String usernameParam) {
        return Responder.build(request -> {
            User logged = authenticate(request);
            String username = request.params(usernameParam);
            User requestedUser = credentialsManager.userByUsername(username);

            if (!logged.equals(requestedUser)) {
                throw new ForbiddenException();
            }

            Collection<ImageMetadata> images = dataProvider.get(requestedUser);
            return new ImagesListMessage(new ImagesList(requestedUser, images));
        });
    }

    public Route getImageDetails(String usernameParam, String imageIdParam) {
        return Responder.build(request -> {
            User logged = authenticate(request);
            String username = request.params(usernameParam);
            User requestedUser = credentialsManager.userByUsername(username);

            if (!logged.equals(requestedUser)) {
                throw new ForbiddenException();
            }

            String imageId = request.params(imageIdParam);
            Optional<Image> image = dataProvider.get(imageId);

            if (!image.isPresent()) {
                throw new NotFoundException();
            }

            if (!image.get().info.owner.equals(logged)) {
                throw new NotFoundException();
            }

            return new ImageDetailsMessage(image.get().info);
        });
    }

    public Route getImageRaw(String usernameParam, String imageIdParam) {
        return Responder.build(request -> {
            User logged = authenticate(request);
            String username = request.params(usernameParam);
            User requestedUser = credentialsManager.userByUsername(username);

            if (!logged.equals(requestedUser)) {
                throw new ForbiddenException();
            }

            String imageId = request.params(imageIdParam);
            Optional<Image> image = dataProvider.get(imageId);

            if (!image.isPresent()) {
                throw new NotFoundException();
            }

            if (!image.get().info.owner.equals(logged)) {
                throw new NotFoundException();
            }

            return new ImageMessage(image.get());
        });
    }

    public Route newImage(String usernameParam) {
        return Responder.build(request -> {
            User logged = authenticate(request);
            String username = request.params(usernameParam);
            User requestedUser = credentialsManager.userByUsername(username);

            if (!logged.equals(requestedUser)) {
                throw new ForbiddenException();
            }

            String id;

            do {
                id = randomUUID().toString();
            } while (dataProvider.contains(id));

            try {
                Part titlePart = request.raw().getPart("title");
                String title = IOUtils.toString(titlePart.getInputStream(), Charset.defaultCharset());

                Part filePart = request.raw().getPart("file");
                InputStream stream = filePart.getInputStream();

                ImageMetadata metadata = new ImageMetadata(id, title, requestedUser);
                Image image = new Image(metadata, IOUtils.toByteArray(stream));
                dataProvider.put(image);

                return new ImageCreationMessage(metadata);

            } catch (IOException | ServletException e) {
                throw new BadRequestException();
            }
        });
    }

    public Route deleteImage(String usernameParam, String imageIdParam) {
        return Responder.build(request -> {
            User logged = authenticate(request);
            String username = request.params(usernameParam);
            User requestedUser = credentialsManager.userByUsername(username);

            if (!logged.equals(requestedUser)) {
                throw new ForbiddenException();
            }

            String imageId = request.params(imageIdParam);
            Optional<Image> image = dataProvider.get(imageId);

            if (!image.isPresent()) {
                throw new NotFoundException();
            }

            if (!image.get().info.owner.equals(logged)) {
                throw new NotFoundException();
            }

            dataProvider.remove(image.get().info.id);

            return new ImageDeletionMessage(image.get().info);
        });
    }

    /**
     * Check if the request contains a proper authentication token and get the user owning it.
     *
     * @param request   request
     * @return user owning the token
     */
    private User authenticate(Request request) {
        Optional<String> authenticationHeader = Optional.ofNullable(request.headers("Authorization"));

        if (!authenticationHeader.isPresent())
            throw new UnauthorizedException();

        String authorization = authenticationHeader.get();

        if (!authorization.startsWith("Bearer"))
            throw new UnauthorizedException();

        String tokenId = authorization.substring("Bearer".length()).trim();
        Optional<Token> token = authorizer.searchToken(tokenId);

        if (!token.isPresent())
            throw new UnauthorizedException();

        try {
            return credentialsManager.userById(token.get().owner.id);
        } catch (RestException e) {
            throw new UnauthorizedException();
        }
    }

}

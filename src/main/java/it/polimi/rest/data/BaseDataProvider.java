package it.polimi.rest.data;

import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.exceptions.BadRequestException;
import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.NotFoundException;
import it.polimi.rest.models.*;
import it.polimi.rest.models.oauth2.OAuth2AccessToken;
import it.polimi.rest.models.oauth2.OAuth2AuthorizationCode;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.OAuth2ClientsList;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class BaseDataProvider implements DataProvider {

    private final Storage storage;
    private final SessionManager sessionManager;

    /**
     * Constructor.
     *
     * @param storage   storage
     */
    public BaseDataProvider(Storage storage, SessionManager sessionManager) {
        this.storage = storage;
        this.sessionManager = sessionManager;
    }

    @Override
    public <T extends Id> T uniqueId(Supplier<String> randomizer, Function<String, T> supplier) {
        return storage.uniqueId(randomizer, supplier);
    }

    @Override
    public User userById(User.Id id) {
        return Optional.ofNullable(storage.userById(id))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public User userByUsername(String username) {
        return Optional.ofNullable(storage.userByUsername(username))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public UsersList users() {
        return storage.users();
    }

    @Override
    public void add(User user) {
        if (user.username == null) {
            throw new BadRequestException("Username not specified");
        } else if (user.password == null) {
            throw new BadRequestException("Password not specified");
        }

        // Username must be unique
        if (storage.userByUsername(user.username) != null) {
            throw new ForbiddenException("Username '" + user.username + "' already in use");
        }

        storage.add(user);
    }

    @Override
    public void update(User user) {
        if (userById(user.id) != null) {
            throw new NotFoundException();
        }

        storage.update(user);
    }

    @Override
    public void remove(User.Id id) {
        User user = userById(id);
        storage.remove(user.id);
    }

    @Override
    public BasicToken basicToken(BasicToken.Id id) {
        return Optional.ofNullable(storage.basicToken(id))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public void add(BasicToken token) {
        storage.add(token);
        sessionManager.add(token);
    }

    @Override
    public void remove(BasicToken.Id id) {
        BasicToken basicToken = basicToken(id);
        storage.remove(basicToken.id);
        sessionManager.remove(basicToken.id);
    }

    @Override
    public Image image(Image.Id id) {
        return Optional.ofNullable(storage.image(id))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public ImagesList images(String username) {
        return storage.images(username);
    }

    @Override
    public void add(Image image) {
        if (image.info.title == null) {
            throw new BadRequestException("Title not specified");
        }

        storage.add(image);
    }

    @Override
    public void remove(Image.Id id) {
        Image image = image(id);
        storage.remove(image.info.id);
    }

    @Override
    public OAuth2Client oAuth2Client(OAuth2Client.Id id) {
        return Optional.ofNullable(storage.oAuth2Client(id))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public OAuth2ClientsList oAuth2Clients(User.Id user) {
        return storage.oAuth2Clients(user);
    }

    @Override
    public void add(OAuth2Client client) {
        if (client.name == null) {
            throw new BadRequestException("Name not specified");
        } else if (client.callback == null) {
            throw new BadRequestException("Callback URL not specified");
        }

        storage.add(client);
    }

    @Override
    public void remove(OAuth2Client.Id id) {
        OAuth2Client client = oAuth2Client(id);
        storage.remove(client.id);
    }

    @Override
    public OAuth2AuthorizationCode oAuth2AuthCode(OAuth2AuthorizationCode.Id id) {
        return Optional.ofNullable(storage.oAuth2AuthCode(id))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public void add(OAuth2AuthorizationCode code) {
        storage.add(code);
    }

    @Override
    public void remove(OAuth2AuthorizationCode.Id id) {
        OAuth2AuthorizationCode code = oAuth2AuthCode(id);
        storage.remove(code.id);
    }

    @Override
    public OAuth2AccessToken oAuth2AccessToken(OAuth2AccessToken.Id id) {
        return Optional.ofNullable(storage.oAuth2AccessToken(id))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public void add(OAuth2AccessToken token) {
        storage.add(token);
        sessionManager.add(token);
    }

    @Override
    public void remove(OAuth2AccessToken.Id id) {
        OAuth2AccessToken token = oAuth2AccessToken(id);
        storage.remove(token.id);
        sessionManager.remove(token.id);
    }

}

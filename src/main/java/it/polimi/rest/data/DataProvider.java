package it.polimi.rest.data;

import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.exceptions.BadRequestException;
import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.NotFoundException;
import it.polimi.rest.models.*;
import it.polimi.rest.models.oauth2.*;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class DataProvider {

    private final Storage storage;
    private final SessionManager sessionManager;

    /**
     * Constructor.
     *
     * @param storage   storage
     */
    public DataProvider(Storage storage, SessionManager sessionManager) {
        this.storage = storage;
        this.sessionManager = sessionManager;
    }

    public <T extends Id> T uniqueId(Supplier<String> randomizer, Function<String, T> supplier) {
        return storage.uniqueId(randomizer, supplier);
    }

    public User userById(User.Id id) {
        return Optional.ofNullable(storage.userById(id))
                .orElseThrow(NotFoundException::new);
    }

    public User userByUsername(String username) {
        return Optional.ofNullable(storage.userByUsername(username))
                .orElseThrow(NotFoundException::new);
    }

    public UsersList users() {
        return new UsersList(storage.users());
    }

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

    public void remove(User.Id id) {
        User user = userById(id);

        storage.basicTokens().forEach(token -> {
            if (token.user.equals(user.id)) {
                remove(token.id);
            }
        });

        storage.images(user.username).forEach(image -> remove(image.id));
        storage.oAuth2Clients(user.id).forEach(client -> remove(client.id));

        storage.oAuth2AuthorizationCodes().forEach(code -> {
            if (code.user.equals(user.id)) {
                remove(user.id);
            }
        });

        storage.oAuth2AccessTokens().forEach(token -> {
            if (token.user.equals(user.id)) {
                remove(token.id);
            }
        });

        storage.remove(user.id);
    }

    public BasicToken basicToken(BasicToken.Id id) {
        return Optional.ofNullable(storage.basicToken(id))
                .orElseThrow(NotFoundException::new);
    }

    public void add(BasicToken token) {
        storage.add(token);
        sessionManager.add(token);
    }

    public void remove(BasicToken.Id id) {
        BasicToken basicToken = basicToken(id);
        storage.remove(basicToken.id);
        sessionManager.remove(basicToken.id);
    }

    public Image image(Image.Id imageId) {
        return Optional.ofNullable(storage.image(imageId))
                .orElseThrow(NotFoundException::new);
    }

    public ImagesList images(String username) {
        User owner = userByUsername(username);
        return new ImagesList(owner, storage.images(username));
    }

    public void add(Image image) {
        if (image.info.title == null) {
            throw new BadRequestException("Title not specified");
        }

        storage.add(image);
    }

    public void remove(Image.Id imageId) {
        Image image = image(imageId);
        storage.remove(image.info.id);
    }

    public OAuth2Client oAuth2Client(OAuth2Client.Id id) {
        return Optional.ofNullable(storage.oAuth2Client(id))
                .orElseThrow(NotFoundException::new);
    }

    public OAuth2ClientsList oAuth2Clients(User.Id user) {
        User owner = userById(user);
        return new OAuth2ClientsList(owner, storage.oAuth2Clients(user));
    }

    public void add(OAuth2Client client) {
        if (client.name == null) {
            throw new BadRequestException("Name not specified");
        } else if (client.callback == null) {
            throw new BadRequestException("Callback URL not specified");
        }

        storage.add(client);
    }

    public void remove(OAuth2Client.Id id) {
        OAuth2Client client = oAuth2Client(id);

        storage.oAuth2AuthorizationCodes().forEach(code -> {
            if (code.client.equals(client.id)) {
                remove(code.id);
            }
        });

        storage.oAuth2AccessTokens().forEach(token -> {
            if (token.client.equals(client.id)) {
                remove(token.id);
            }
        });

        storage.remove(client.id);
    }

    public OAuth2AuthorizationCode oAuth2AuthCode(OAuth2AuthorizationCode.Id id) {
        return Optional.ofNullable(storage.oAuth2AuthCode(id))
                .orElseThrow(NotFoundException::new);
    }

    public void add(OAuth2AuthorizationCode code) {
        storage.add(code);
    }

    public void remove(OAuth2AuthorizationCode.Id id) {
        OAuth2AuthorizationCode code = oAuth2AuthCode(id);
        storage.remove(code.id);
    }

    public OAuth2AccessToken oAuth2AccessToken(OAuth2AccessToken.Id id) {
        return Optional.ofNullable(storage.oAuth2AccessToken(id))
                .orElseThrow(NotFoundException::new);
    }

    public void add(OAuth2AccessToken token) {
        storage.add(token);
        sessionManager.add(token);
    }

    public void remove(OAuth2AccessToken.Id id) {
        OAuth2AccessToken token = oAuth2AccessToken(id);
        storage.remove(token.id);
        sessionManager.remove(token.id);
    }

    public OAuth2RefreshToken oAuth2RefreshToken(OAuth2RefreshToken.Id id) {
        return Optional.ofNullable(storage.oAuth2RefreshToken(id))
                .orElseThrow(NotFoundException::new);
    }

    public void add(OAuth2RefreshToken token) {
        storage.add(token);
    }

    public void remove(OAuth2RefreshToken.Id id) {
        OAuth2RefreshToken token = oAuth2RefreshToken(id);
        storage.remove(token.id);
    }

}

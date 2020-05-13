package it.polimi.rest.authorization;

import it.polimi.rest.data.DataProvider;
import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.*;
import it.polimi.rest.models.oauth2.*;

import java.util.function.Function;
import java.util.function.Supplier;

import static it.polimi.rest.exceptions.UnauthorizedException.AuthType.BEARER;

class SecureDataProvider implements DataProvider {

    private final DataProvider dataProvider;
    private final Authorizer authorizer;
    private final SessionManager sessionManager;
    private final Agent agent;

    public SecureDataProvider(DataProvider dataProvider, SessionManager sessionManager, Authorizer authorizer, Agent agent) {
        this.dataProvider = dataProvider;
        this.authorizer = authorizer;
        this.sessionManager = sessionManager;
        this.agent = agent;
    }

    @Override
    public <T extends Id> T uniqueId(Supplier<String> randomizer, Function<String, T> supplier) {
        return dataProvider.uniqueId(randomizer, supplier);
    }

    @Override
    public User userById(User.Id id) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        User user = dataProvider.userById(id);

        if (!authorizer.get(id, agent).read) {
            throw new ForbiddenException();
        }

        return user;
    }

    @Override
    public User userByUsername(String username) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        User user = dataProvider.userByUsername(username);

        if (!authorizer.get(user.id, agent).read) {
            throw new ForbiddenException();
        }

        return user;
    }

    @Override
    public UsersList users() {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        return dataProvider.users();
    }

    @Override
    public void add(User user) {
        dataProvider.add(user);

        authorizer.grant(user.id, user.id, Permission.WRITE);
        authorizer.grant(ImagesList.placeholder(user), user.id, Permission.WRITE);
        authorizer.grant(OAuth2ClientsList.placeholder(user), user.id, Permission.WRITE);
    }

    @Override
    public void remove(User.Id id) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        User user = userById(id);

        if (!authorizer.get(user.id, agent).write) {
            throw new ForbiddenException();
        }

        dataProvider.remove(id);

        authorizer.remove(id);
    }

    @Override
    public BasicToken basicToken(BasicToken.Id id) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        BasicToken token = dataProvider.basicToken(id);

        if (!authorizer.get(token.id, agent).read) {
            throw new ForbiddenException();
        }

        return token;
    }

    @Override
    public void add(BasicToken token) {
        dataProvider.add(token);
        authorizer.grant(token.id, token.user, Permission.WRITE);
    }

    @Override
    public void remove(BasicToken.Id id) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        BasicToken token = basicToken(id);

        if (!authorizer.get(token.id, agent).write) {
            throw new ForbiddenException();
        }

        dataProvider.remove(id);

        authorizer.remove(id);
    }

    @Override
    public Image image(Image.Id imageId) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        Image image = dataProvider.image(imageId);

        if (!authorizer.get(image.info.id, agent).read) {
            throw new ForbiddenException();
        }

        return image;
    }

    @Override
    public ImagesList images(String username) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        ImagesList images = dataProvider.images(username);

        if (!authorizer.get(images, agent).read) {
            throw new ForbiddenException();
        }

        return images;
    }

    @Override
    public void add(Image image) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        // Check if the agent has write access to the user images list
        if (!authorizer.get(ImagesList.placeholder(image.info.owner), agent).write) {
            throw new ForbiddenException();
        }

        dataProvider.add(image);
        authorizer.grant(image.info.id, image.info.owner.id, Permission.WRITE);
    }

    @Override
    public void remove(Image.Id imageId) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        Image image = image(imageId);

        if (!authorizer.get(image.info.id, agent).write) {
            throw new ForbiddenException();
        }

        dataProvider.remove(imageId);
        authorizer.remove(imageId);
    }

    @Override
    public OAuth2Client oAuth2Client(OAuth2Client.Id id) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        OAuth2Client client = dataProvider.oAuth2Client(id);

        if (!authorizer.get(client.id, agent).read) {
            throw new ForbiddenException();
        }

        return client;
    }

    @Override
    public OAuth2ClientsList oAuth2Clients(User.Id user) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        OAuth2ClientsList clients = dataProvider.oAuth2Clients(user);

        if (!authorizer.get(clients, agent).read) {
            throw new ForbiddenException();
        }

        return clients;
    }

    @Override
    public void add(OAuth2Client client) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        User owner = userById(client.owner.id);

        if (!authorizer.get(OAuth2ClientsList.placeholder(owner), agent).write) {
            throw new ForbiddenException();
        }

        dataProvider.add(client);

        authorizer.grant(client.id, owner.id, Permission.WRITE);
        authorizer.grant(client.id, client.id, Permission.READ);
    }

    @Override
    public void remove(OAuth2Client.Id id) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        OAuth2Client client = oAuth2Client(id);

        if (!authorizer.get(client.id, agent).write) {
            throw new ForbiddenException();
        }

        dataProvider.remove(id);
        authorizer.remove(id);
    }

    @Override
    public OAuth2AuthorizationCode oAuth2AuthCode(OAuth2AuthorizationCode.Id id) {
        return dataProvider.oAuth2AuthCode(id);
    }

    @Override
    public void add(OAuth2AuthorizationCode code) {
        dataProvider.add(code);
    }

    @Override
    public void remove(OAuth2AuthorizationCode.Id id) {
        dataProvider.remove(id);
    }

    @Override
    public OAuth2AccessToken oAuth2AccessToken(OAuth2AccessToken.Id id) {
        return dataProvider.oAuth2AccessToken(id);
    }

    @Override
    public void add(OAuth2AccessToken token) {
        dataProvider.add(token);
        token.scope.forEach(scope -> scope.addPermissions(authorizer, sessionManager, token));
    }

    @Override
    public void remove(OAuth2AccessToken.Id id) {
        dataProvider.remove(id);
        authorizer.remove(id);
    }

    @Override
    public OAuth2RefreshToken oAuth2RefreshToken(OAuth2RefreshToken.Id id) {
        return dataProvider.oAuth2RefreshToken(id);
    }

    @Override
    public void add(OAuth2RefreshToken token) {
        dataProvider.add(token);
    }

    @Override
    public void remove(OAuth2RefreshToken.Id id) {
        dataProvider.remove(id);
    }

}

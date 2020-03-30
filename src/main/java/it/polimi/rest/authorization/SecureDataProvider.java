package it.polimi.rest.authorization;

import it.polimi.rest.data.DataProvider;
import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.*;
import it.polimi.rest.models.oauth2.OAuth2AccessToken;
import it.polimi.rest.models.oauth2.OAuth2AuthorizationCode;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.OAuth2ClientsList;

import java.util.function.Function;
import java.util.function.Supplier;

import static it.polimi.rest.exceptions.UnauthorizedException.AuthType.BASIC;
import static it.polimi.rest.exceptions.UnauthorizedException.AuthType.BEARER;

class SecureDataProvider implements DataProvider {

    private final DataProvider dataProvider;
    private final Authorizer authorizer;
    private final Agent agent;

    public SecureDataProvider(DataProvider dataProvider, Authorizer authorizer, Agent agent) {
        this.dataProvider = dataProvider;
        this.authorizer = authorizer;
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
    public void update(User user) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        User u = userById(user.id);

        if (!authorizer.get(u.id, agent).write) {
            throw new ForbiddenException();
        }

        dataProvider.update(user);
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
    }

    @Override
    public Image image(Image.Id id) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        Image image = dataProvider.image(id);

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
        authorizer.grant(image.info.id, agent, Permission.WRITE);
    }

    @Override
    public void remove(Image.Id id) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        Image image = image(id);

        if (!authorizer.get(image.info.id, agent).write) {
            throw new ForbiddenException();
        }

        dataProvider.remove(id);
        authorizer.removeObject(id);
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
        authorizer.removeObject(id);
    }

    @Override
    public OAuth2AuthorizationCode oAuth2AuthCode(OAuth2AuthorizationCode.Id id) {
        if (agent == null) {
            throw new UnauthorizedException(BASIC);
        }

        OAuth2AuthorizationCode code = dataProvider.oAuth2AuthCode(id);

        if (!authorizer.get(code.id, agent).read) {
            throw new ForbiddenException();
        }

        return code;
    }

    @Override
    public void add(OAuth2AuthorizationCode code) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        OAuth2Client client = oAuth2Client(code.client);

        if (!authorizer.get(client.id, agent).write) {
            throw new ForbiddenException();
        }

        dataProvider.add(code);
        authorizer.grant(code.id, code.client, Permission.WRITE);
    }

    @Override
    public void remove(OAuth2AuthorizationCode.Id id) {
        if (agent == null) {
            throw new ForbiddenException();
        }

        OAuth2AuthorizationCode code = oAuth2AuthCode(id);

        if (!authorizer.get(code.id, agent).write) {
            throw new ForbiddenException();
        }

        dataProvider.remove(code.id);
        authorizer.removeObject(code.id);
    }

    @Override
    public OAuth2AccessToken oAuth2AccessToken(OAuth2AccessToken.Id id) {
        return dataProvider.oAuth2AccessToken(id);
    }

    @Override
    public void add(OAuth2AccessToken token) {
        dataProvider.add(token);
        token.scope.forEach(scope -> scope.addPermissions(authorizer, dataProvider, token));
    }

    @Override
    public void remove(OAuth2AccessToken.Id id) {
        OAuth2AccessToken token = oAuth2AccessToken(id);

        dataProvider.remove(id);
        authorizer.removeAgent(token);
    }

}

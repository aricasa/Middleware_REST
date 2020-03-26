package it.polimi.rest.authorization;

import it.polimi.rest.data.DataProvider;
import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.*;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.OAuth2ClientId;
import it.polimi.rest.models.oauth2.OAuth2ClientsList;

import java.util.function.Function;

import static it.polimi.rest.exceptions.UnauthorizedException.AuthType.BEARER;

class SecureDataProvider implements DataProvider {

    private final DataProvider dataProvider;
    private final Authorizer authorizer;
    private final Token token;

    public SecureDataProvider(DataProvider dataProvider, Authorizer authorizer, Token token) {
        this.dataProvider = dataProvider;
        this.authorizer = authorizer;
        this.token = token;
    }

    @Override
    public <T extends Id> T uniqueId(Function<String, T> supplier) {
        return dataProvider.uniqueId(supplier);
    }

    @Override
    public User userById(UserId id) {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException(BEARER);
        }

        User user = dataProvider.userById(id);

        if (!authorizer.get(id, token.agent()).read) {
            throw new ForbiddenException();
        }

        return user;
    }

    @Override
    public User userByUsername(String username) {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException(BEARER);
        }

        User user = dataProvider.userByUsername(username);

        if (!authorizer.get(user.id, token.agent()).read) {
            throw new ForbiddenException();
        }

        return user;
    }

    @Override
    public UsersList users() {
        if (token == null || !token.isValid()) {
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
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException(BEARER);
        }

        User u = userById(user.id);

        if (!authorizer.get(u.id, token.agent()).write) {
            throw new ForbiddenException();
        }

        dataProvider.update(user);
    }

    @Override
    public void remove(UserId id) {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException(BEARER);
        }

        User user = userById(id);

        if (!authorizer.get(user.id, token.agent()).write) {
            throw new ForbiddenException();
        }

        dataProvider.remove(id);

        // TODO: remove sessions
        // TODO: remove images
        // TODO: remove OAuth2 clients
    }

    @Override
    public Image image(ImageId id) {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException(BEARER);
        }

        Image image = dataProvider.image(id);

        if (!authorizer.get(image.info.id, token.agent()).read) {
            throw new ForbiddenException();
        }

        return image;
    }

    @Override
    public ImagesList images(UserId user) {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException(BEARER);
        }

        ImagesList images = dataProvider.images(user);

        if (!authorizer.get(images, token.agent()).read) {
            throw new ForbiddenException();
        }

        return images;
    }

    @Override
    public void add(Image image) {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException(BEARER);
        }

        // Check if the agent has write access to the user images list
        if (!authorizer.get(ImagesList.placeholder(image.info.owner), token.agent()).write) {
            throw new ForbiddenException();
        }

        dataProvider.add(image);
        authorizer.grant(image.info.id, token.agent(), Permission.WRITE);
    }

    @Override
    public void remove(ImageId id) {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException(BEARER);
        }

        Image image = image(id);

        if (!authorizer.get(image.info.id, token.agent()).write) {
            throw new ForbiddenException();
        }

        dataProvider.remove(id);
        authorizer.revokeAll(id);
    }

    @Override
    public OAuth2Client oAuth2Client(OAuth2ClientId id) {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException(BEARER);
        }

        OAuth2Client client = dataProvider.oAuth2Client(id);

        if (!authorizer.get(client.id, token.agent()).read) {
            throw new ForbiddenException();
        }

        return client;
    }

    @Override
    public OAuth2ClientsList oAuth2Clients(UserId user) {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException(BEARER);
        }

        OAuth2ClientsList clients = dataProvider.oAuth2Clients(user);

        if (!authorizer.get(clients, token.agent()).read) {
            throw new ForbiddenException();
        }

        return clients;
    }

    @Override
    public void add(OAuth2Client client) {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException(BEARER);
        }

        User owner = userById(client.owner.id);

        if (!authorizer.get(OAuth2ClientsList.placeholder(owner), token.agent()).write) {
            throw new ForbiddenException();
        }

        dataProvider.add(client);
        authorizer.grant(client.id, owner.id, Permission.WRITE);
        authorizer.grant(client.id, client.id, Permission.READ);
    }

    @Override
    public void remove(OAuth2ClientId id) {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException(BEARER);
        }

        OAuth2Client client = oAuth2Client(id);

        if (!authorizer.get(client.id, token.agent()).write) {
            throw new ForbiddenException();
        }

        dataProvider.remove(id);
        authorizer.revokeAll(id);
    }

}

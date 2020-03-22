package it.polimi.rest.authorization;

import it.polimi.rest.data.DataProvider;
import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.*;

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

        if (!authorizer.get(id, token.agent()).write) {
            throw new ForbiddenException();
        }

        dataProvider.remove(id);
        authorizer.revokeAll(id);
    }

    @Override
    public void add(OAuthClient client) {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException(BEARER);
        }

        //if (!authorizer.check(token, client.owner).write) {
        //    throw new ForbiddenException();
        //}

        dataProvider.add(client);
    }

}

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

        if (!authorizer.check(token, user).read) {
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

        if (!authorizer.check(token, user).read) {
            throw new ForbiddenException();
        }

        return user;
    }

    @Override
    public UsersList users() {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException(BEARER);
        }

        UsersList users = dataProvider.users();

        if (!authorizer.check(token, users).read) {
            throw new ForbiddenException();
        }

        return users;
    }

    @Override
    public void add(User user) {
        dataProvider.add(user);
    }

    @Override
    public void update(User user) {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException(BEARER);
        }

        User u = userById(user.id);

        if (!authorizer.check(token, u).write) {
            throw new ForbiddenException();
        }

        dataProvider.update(user);
    }

    @Override
    public void remove(UserId userId) {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException(BEARER);
        }

        User u = userById(userId);

        if (!authorizer.check(token, u).write) {
            throw new ForbiddenException();
        }

        dataProvider.remove(userId);
    }

    @Override
    public Image image(ImageId id) {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException(BEARER);
        }

        Image image = dataProvider.image(id);

        if (!authorizer.check(token, image).read) {
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

        if (!authorizer.check(token, images).read) {
            throw new ForbiddenException();
        }

        return images;
    }

    @Override
    public void add(Image image) {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException(BEARER);
        }

        if (!authorizer.check(token, image.info.owner).write) {
            throw new ForbiddenException();
        }

        dataProvider.add(image);
    }

    @Override
    public void remove(ImageId imageId) {
        Image image = image(imageId);

        if (!authorizer.check(token, image).write) {
            throw new ForbiddenException();
        }

        dataProvider.remove(imageId);
    }

}

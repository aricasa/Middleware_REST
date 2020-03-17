package it.polimi.rest.authorization;

import it.polimi.rest.data.DataProvider;
import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.*;

import java.util.Optional;

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
    public boolean contains(ImageId id) {
        return dataProvider.contains(id);
    }

    @Override
    public ImageId getUniqueId() {
        return dataProvider.getUniqueId();
    }

    @Override
    public Image image(ImageId id) {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException();
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
            throw new UnauthorizedException();
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
            throw new UnauthorizedException();
        }

        if (!authorizer.check(token, image.info.owner).write) {
            throw new ForbiddenException();
        }

        dataProvider.add(image);
    }

    @Override
    public void remove(ImageId id) {
        Image image = image(id);

        if (!authorizer.check(token, image).write) {
            throw new ForbiddenException();
        }

        dataProvider.remove(id);
    }

}

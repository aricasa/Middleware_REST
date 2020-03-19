package it.polimi.rest.authorization;

import it.polimi.rest.models.*;

public class SimpleAuthorizer implements Authorizer {

    @Override
    public Permission check(Token token, Image item) {
        return check(token, item.info);
    }

    @Override
    public Permission check(Token token, ImageMetadata item) {
        if (!token.user.equals(item.owner.id)) {
            return Permission.NONE;
        }

        return token.imagesPermission;
    }

    @Override
    public Permission check(Token token, ImagesList item) {
        if (!token.user.equals(item.owner.id)) {
            return Permission.NONE;
        }

        return token.imagesPermission;
    }

    @Override
    public Permission check(Token token, Root item) {
        return Permission.READ;
    }

    @Override
    public Permission check(Token token, Token item) {
        if (!token.user.equals(item.user)) {
            return Permission.NONE;
        }

        return token.sessionPermission;
    }

    @Override
    public Permission check(Token token, User item) {
        return check(token, item.id);
    }

    @Override
    public Permission check(Token token, UserId item) {
        if (!token.user.equals(item)) {
            return Permission.NONE;
        }

        return token.accountPermission;
    }

    @Override
    public Permission check(Token token, UsersList item) {
        return Permission.READ;
    }

}

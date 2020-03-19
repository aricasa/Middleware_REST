package it.polimi.rest.authorization;

import it.polimi.rest.models.*;

public class SimpleAuthorizer implements Authorizer {

    @Override
    public Permission check(Token token, Image item) {
        return check(token, item.info);
    }

    @Override
    public Permission check(Token token, ImageMetadata item) {
        if (token.owner.equals(item.owner.id)) {
            return Permission.WRITE;

        } else if (token.readableUser.equals(item.owner.id)) {
            return Permission.READ;

        } else {
            return Permission.NONE;
        }
    }

    @Override
    public Permission check(Token token, ImagesList item) {
        if (token.owner.equals(item.owner.id)) {
            return Permission.WRITE;

        } else if (token.readableUser.equals(item.owner.id)) {
            return Permission.READ;

        } else {
            return Permission.NONE;
        }
    }

    @Override
    public Permission check(Token token, Root item) {
        return Permission.READ;
    }

    @Override
    public Permission check(Token token, Token item) {
        if (token.owner.equals(item.owner)) {
            return Permission.WRITE;

        } else if (token.readableUser.equals(item.owner)) {
            return Permission.READ;

        } else {
            return Permission.NONE;
        }
    }

    @Override
    public Permission check(Token token, User item) {
        return check(token, item.id);
    }

    @Override
    public Permission check(Token token, UserId item) {
        if (token.owner.equals(item)) {
            return Permission.WRITE;

        } else if (token.readableUser.equals(item)) {
            return Permission.READ;

        } else {
            return Permission.NONE;
        }
    }

    @Override
    public Permission check(Token token, UsersList item) {
        return Permission.READ;
    }

}

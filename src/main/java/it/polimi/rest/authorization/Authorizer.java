package it.polimi.rest.authorization;

import it.polimi.rest.models.*;

public interface Authorizer {

    Permission check(Token token, Image item);
    Permission check(Token token, ImageMetadata item);
    Permission check(Token token, ImagesList item);
    Permission check(Token token, Root item);
    Permission check(Token token, Token item);
    Permission check(Token token, User item);
    Permission check(Token token, UserId item);
    Permission check(Token token, UsersList item);

}

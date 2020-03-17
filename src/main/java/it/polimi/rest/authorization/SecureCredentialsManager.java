package it.polimi.rest.authorization;

import it.polimi.rest.credentials.CredentialsManager;
import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.*;

class SecureCredentialsManager implements CredentialsManager {

    private final CredentialsManager credentialsManager;
    private final Authorizer authorizer;
    private final Token token;

    public SecureCredentialsManager(CredentialsManager credentialsManager, Authorizer authorizer, Token token) {
        this.credentialsManager = credentialsManager;
        this.authorizer = authorizer;
        this.token = token;
    }

    @Override
    public UserId getUniqueId() {
        return credentialsManager.getUniqueId();
    }

    @Override
    public UsersList users() {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException();
        }

        UsersList result = credentialsManager.users();

        if (!authorizer.check(token, result).read) {
            throw new ForbiddenException();
        }

        return result;
    }

    @Override
    public User userById(UserId id) {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException();
        }

        User user = credentialsManager.userById(id);

        if (!authorizer.check(token, user).read) {
            throw new ForbiddenException();
        }

        return user;
    }

    @Override
    public User userByUsername(String username) {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException();
        }

        User user = credentialsManager.userByUsername(username);

        if (!authorizer.check(token, user).read) {
            throw new ForbiddenException();
        }

        return user;
    }

    @Override
    public User authenticate(String username, String password) {
        return credentialsManager.authenticate(username, password);
    }

    @Override
    public void add(User user) {
        credentialsManager.add(user);
    }

    @Override
    public void update(User user) {
        User u = userById(user.id);

        if (!authorizer.check(token, u).write) {
            throw new ForbiddenException();
        }

        credentialsManager.update(user);
    }

    @Override
    public void remove(UserId id) {
        User user = userById(id);

        if (!authorizer.check(token, user).write) {
            throw new ForbiddenException();
        }

        credentialsManager.remove(id);
    }

}

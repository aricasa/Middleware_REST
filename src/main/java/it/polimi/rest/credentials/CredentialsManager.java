package it.polimi.rest.credentials;

import it.polimi.rest.Logger;
import it.polimi.rest.exceptions.NotFoundException;
import it.polimi.rest.models.Token;
import it.polimi.rest.models.User;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.UnauthorizedException;

import java.util.Collection;
import java.util.Optional;

import static java.util.UUID.randomUUID;

public abstract class CredentialsManager {

    private final Logger logger = new Logger(this.getClass().getSimpleName());
    private final Authorizer authorizer;

    public CredentialsManager(Authorizer authorizer) {
        this.authorizer = authorizer;
    }

    protected abstract void add(User user);
    protected abstract void remove(User id);

    public abstract Collection<User> users();
    protected abstract Optional<User> getByUsername(String username);
    protected abstract Optional<User> getById(String id);

    public final User userByUsername(String username) {
        Optional<User> user = getByUsername(username);
        return user.orElseThrow(NotFoundException::new);
    }

    public final User userById(String id) {
        Optional<User> user = getById(id);
        return user.orElseThrow(NotFoundException::new);
    }

    /**
     * Check the user credentials and, if valid, generate a
     * token for the user to operate in the system.
     *
     * @param user  user
     * @return token
     */
    public final Token login(User user) {
        User account = userByUsername(user.username);

        String storedPassword = account.password;

        if (!storedPassword.equals(user.password)) {
            logger.w("Login: wrong password for user " + user.password);
            throw new UnauthorizedException("Wrong credentials");
        }

        return authorizer.authorize(account);
    }

    /**
     * Create a new user.
     *
     * @param user  new user
     * @return the created user with its ID set
     */
    public final User signup(User user) {
        if (getByUsername(user.username).isPresent()) {
            logger.w("Signup: " + user.username + " already in use");
            throw new ForbiddenException("Username already in use");
        }

        User registered = new User(newId(), user.username, user.password);
        add(registered);
        return registered;
    }

    public final void delete(User user) {
        authorizer.revoke(user);
        remove(user);
    }

    /**
     * Generate an ID that is not in use by anyone else.
     *
     * @return ID
     */
    private String newId() {
        String id;

        do {
            id = randomUUID().toString().split("-")[0];
        } while (getById(id).isPresent());

        return id;
    }

}

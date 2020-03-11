package it.polimi.rest.credentials;

import it.polimi.rest.Logger;
import it.polimi.rest.authorization.Token;
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

    protected abstract void add(String id, User user);
    protected abstract void remove(String id);

    public abstract Collection<User> users();
    public abstract Optional<User> userById(String id);
    public abstract Optional<User> userByUsername(String username);

    /**
     * Check the user credentials and, if valid, generate a
     * token for the user to operate in the system.
     *
     * @param user  user
     * @return token
     */
    public final Token login(User user) {
        Optional<User> account = userByUsername(user.getUsername());

        if (!account.isPresent()) {
            logger.w("Login: user " + user.getUsername() + " doesn't exist");
            throw new UnauthorizedException("Wrong credentials");
        }

        String storedPassword = account.get().getPassword();

        if (!storedPassword.equals(user.getPassword())) {
            logger.w("Login: wrong password for user " + user.getUsername());
            throw new UnauthorizedException("Wrong credentials");
        }

        return authorizer.authorize(account.get());
    }

    /**
     * Create a new user.
     *
     * @param user  new user
     * @return the created user with its ID set
     */
    public final User signup(User user) {
        if (userByUsername(user.getUsername()).isPresent()) {
            logger.w("Signup: " + user.getUsername() + " already in use");
            throw new ForbiddenException("Username already in use");
        }

        User registered = new User(newId(), user.getUsername(), user.getPassword());
        add(registered.getId(), registered);
        return registered;
    }

    public final void delete(User user) {

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
        } while (userById(id).isPresent());

        return id;
    }

}

package it.polimi.rest.authentication;

import it.polimi.rest.data.Storage;
import it.polimi.rest.exceptions.BadRequestException;
import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.User;

import java.util.Optional;

import static it.polimi.rest.exceptions.UnauthorizedException.AuthType.BASIC;

public final class Authenticator {

    private final Storage storage;

    /**
     * Constructor.
     *
     * @param storage   storage
     */
    public Authenticator(Storage storage) {
        this.storage = storage;
    }

    /**
     * Check if the credentials are valid.
     *
     * @param username  username
     * @param password  password
     *
     * @return user ID
     * @throws UnauthorizedException if the credentials are wrong
     */
    public User.Id authenticate(String username, String password) {
        if (username == null) {
            throw new BadRequestException("Username not specified");
        } else if (password == null) {
            throw new BadRequestException("Password not specified");
        }

        return Optional.of(storage.userByUsername(username))
                .filter(user -> user.username.equals(username) && user.password.equals(password))
                .orElseThrow( () -> new UnauthorizedException(BASIC, "Wrong credentials"))
                .id;
    }

}

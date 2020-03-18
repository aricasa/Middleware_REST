package it.polimi.rest.credentials;

import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.UserId;

public interface CredentialsManager {

    /**
     * Check if the password is correct.
     *
     * @param username  username
     * @param password  password
     *
     * @return user ID
     * @throws UnauthorizedException if the credentials are wrong
     */
    UserId authenticate(String username, String password);

    void add(UserId user, String username, String password);

    void update(UserId user, String username, String password);

    void remove(UserId user);

}

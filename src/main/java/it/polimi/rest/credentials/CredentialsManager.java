package it.polimi.rest.credentials;

import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.User;

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
    User.Id authenticate(String username, String password);

    void add(User.Id user, String username, String password);

    void update(User.Id user, String username, String password);

    void remove(User.Id user);

}

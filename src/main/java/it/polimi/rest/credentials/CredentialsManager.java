package it.polimi.rest.credentials;

import it.polimi.rest.models.User;
import it.polimi.rest.models.UserId;
import it.polimi.rest.models.UsersList;

import java.util.Collection;
import java.util.Optional;

public interface CredentialsManager {

    /**
     * Get a new ID that is guaranteed not to be used by any other user.
     *
     * @return ID
     */
    UserId getUniqueId();

    /**
     * Get all the users.
     *
     * @return users
     */
    UsersList users();

    /**
     * Get user by ID.
     *
     * @param id    user ID
     * @return user
     */
    User userById(UserId id);

    /**
     * Get user by username.
     *
     * @param username  username
     * @return user
     */
    User userByUsername(String username);

    /**
     * Check if the credentials (username and password) match with
     * the ones already registered.
     *
     * @param username  username
     * @param password  password
     *
     * @return user matching the credentials
     */
    User authenticate(String username, String password);

    /**
     * Add a new user.
     *
     * @param user  user to be added
     */
    void add(User user);

    /**
     * Update a user.
     *
     * @param user  user to be updated
     */
    void update(User user);

    /**
     * Remove a user.
     *
     * @param id    ID of the user to be removed
     */
    void remove(UserId id);

}

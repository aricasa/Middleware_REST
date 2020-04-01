package it.polimi.rest.authentication;

import it.polimi.rest.data.DataProvider;
import it.polimi.rest.exceptions.BadRequestException;
import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.User;

import static it.polimi.rest.exceptions.UnauthorizedException.AuthType.BASIC;

public final class CredentialsManager {

    private final DataProvider dataProvider;

    /**
     * Constructor.
     *
     * @param dataProvider  unsecured data provider
     */
    public CredentialsManager(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    /**
     * Check if the password is correct.
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

        return dataProvider.users().stream()
                .filter(user -> user.username.equals(username) && user.password.equals(password))
                .findFirst()
                .orElseThrow( () -> new UnauthorizedException(BASIC, "Wrong credentials"))
                .id;
    }

}

package it.polimi.rest.credentials;

import it.polimi.rest.exceptions.BadRequestException;
import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.NotFoundException;
import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.UserId;
import it.polimi.rest.utils.Pair;

import java.util.*;

import static it.polimi.rest.exceptions.UnauthorizedException.AuthType.BASIC;

public class VolatileCredentialsManager implements CredentialsManager {

    private final Map<UserId, Pair<String, String>> credentials = new HashMap<>();

    @Override
    public UserId authenticate(String username, String password) {
        if (username == null) {
            throw new BadRequestException("Username not specified");
        } else if (password == null) {
            throw new BadRequestException("Password not specified");
        }

        return credentials.entrySet().stream()
                .filter(cred -> cred.getValue().first.equals(username) && cred.getValue().second.equals(password))
                .findFirst()
                .orElseThrow( () -> new UnauthorizedException(BASIC, "Wrong credentials"))
                .getKey();
    }

    @Override
    public void add(UserId id, String username, String password) {
        if (username == null) {
            throw new BadRequestException("Username not specified");
        } else if (password == null) {
            throw new BadRequestException("Password not specified");
        }

        if (credentials.containsKey(id)) {
            throw new ForbiddenException();
        }

        credentials.put(id, new Pair<>(username, password));
    }

    @Override
    public void update(UserId id, String username, String password) {
        if (!credentials.containsKey(id)) {
            throw new NotFoundException();
        }

        credentials.put(id, new Pair<>(username, password));
    }

    @Override
    public void remove(UserId user) {
        if (!credentials.containsKey(user)) {
            throw new NotFoundException();
        }

        credentials.remove(user);
    }

}

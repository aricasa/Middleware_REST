package it.polimi.rest.credentials;

import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.NotFoundException;
import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.User;
import it.polimi.rest.models.UserId;
import it.polimi.rest.models.UsersList;

import java.util.*;

import static java.util.UUID.randomUUID;

public class VolatileCredentialsManager implements CredentialsManager {

    private final Map<UserId, User> users = new HashMap<>();
    private final Collection<UserId> reserved = new HashSet<>();

    @Override
    public synchronized UserId getUniqueId() {
        UserId id;

        do {
            id = new UserId(randomUUID().toString().split("-")[0]);
        } while (users.containsKey(id) || reserved.contains(id));

        // Reserve the ID
        reserved.add(id);

        return id;
    }

    @Override
    public synchronized UsersList users() {
        return new UsersList(users.values());
    }

    @Override
    public synchronized User userById(UserId id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException();
        }

        return users.get(id);
    }

    @Override
    public synchronized User userByUsername(String username) {
        return users.values().stream()
                .filter(user -> user.username.equals(username))
                .findFirst().orElseThrow(NotFoundException::new);
    }

    @Override
    public synchronized User authenticate(String username, String password) {
        User user = userByUsername(username);

        if (user.password.equals(password)) {
            return user;
        } else {
            throw new UnauthorizedException();
        }
    }

    @Override
    public synchronized void add(User user) {
        if (users.containsKey(user.id)) {
            throw new ForbiddenException("User " + user + " already exists");
        }

        users.put(user.id, user);
        reserved.remove(user.id);
    }

    @Override
    public synchronized void update(User user) {
        if (users.containsKey(user.id)) {
            users.put(user.id, user);
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public void remove(UserId id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException();
        }

        users.remove(id);
    }

}

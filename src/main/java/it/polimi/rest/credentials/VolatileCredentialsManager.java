package it.polimi.rest.credentials;

import it.polimi.rest.models.User;
import it.polimi.rest.models.UserId;

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
        } while (userById(id).isPresent() || reserved.contains(id));

        // Reserve the ID
        reserved.add(id);

        return id;
    }

    @Override
    public synchronized Collection<User> users() {
        return users.values();
    }

    @Override
    public synchronized Optional<User> userById(UserId id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public synchronized Optional<User> userByUsername(String username) {
        return users.values().stream()
                .filter(user -> user.username.equals(username))
                .findFirst();
    }

    @Override
    public Optional<User> authenticate(String username, String password) {
        Optional<User> user = userByUsername(username);

        if (user.isPresent() && user.get().password.equals(password)) {
            return user;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void add(User user) {
        users.putIfAbsent(user.id, user);
        reserved.remove(user.id);
    }

    @Override
    public void update(User user) {
        if (users.containsKey(user.id)) {
            users.put(user.id, user);
        }
    }

    @Override
    public void remove(UserId id) {
        users.remove(id);
    }

}

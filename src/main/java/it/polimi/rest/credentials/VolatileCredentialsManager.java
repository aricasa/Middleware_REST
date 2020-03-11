package it.polimi.rest.credentials;

import it.polimi.rest.models.User;
import it.polimi.rest.authorization.Authorizer;

import java.util.*;

public final class VolatileCredentialsManager extends CredentialsManager {

    private final Map<String, User> users = new HashMap<>();

    public VolatileCredentialsManager(Authorizer authorizer) {
        super(authorizer);
    }

    @Override
    public Optional<User> userById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> userByUsername(String username) {
        return users.values().stream().filter(user -> user.getUsername().equals(username)).findAny();
    }

    @Override
    public Collection<User> users() {
        return Collections.unmodifiableCollection(users.values());
    }

    @Override
    protected void add(String id, User user) {
        users.put(id, user);
    }

    @Override
    protected void remove(String id) {
        users.remove(id);
    }

}

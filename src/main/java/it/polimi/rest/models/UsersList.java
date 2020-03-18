package it.polimi.rest.models;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class UsersList implements Model {

    private final Collection<User> users;

    public UsersList(Collection<User> users) {
        this.users = users;
    }

    @Override
    public Optional<String> self() {
        return Optional.of("/users");
    }

    @Override
    public Map<String, Link> links() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> embedded() {
        return Collections.singletonMap("item", users);
    }

}

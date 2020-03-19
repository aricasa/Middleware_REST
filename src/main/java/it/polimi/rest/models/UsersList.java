package it.polimi.rest.models;

import com.google.gson.annotations.Expose;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class UsersList implements Model {

    private final Collection<User> users;

    @Expose
    public final int count;

    public UsersList(Collection<User> users) {
        this.users = users;
        this.count = users.size();
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

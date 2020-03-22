package it.polimi.rest.models;

import com.google.gson.annotations.Expose;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class UsersList implements Model, Iterable<User> {

    public final Collection<User> users;

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

    @Override
    public Iterator<User> iterator() {
        return users.iterator();
    }

    @Override
    public void forEach(Consumer<? super User> action) {
        users.forEach(action);
    }

    @Override
    public Spliterator<User> spliterator() {
        return users.spliterator();
    }

    public Stream<User> stream() {
        return users.stream();
    }

}

package it.polimi.rest.models;

import com.google.gson.annotations.Expose;
import it.polimi.rest.messages.Link;

import java.util.*;

public class User implements Model {

    @Expose(deserialize = false)
    public final String id;

    @Expose
    public final String username;

    @Expose(serialize = false)
    public final String password;

    public User(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return "{" + id + ", " + username + "}";
    }

    @Override
    public Optional<String> self() {
        return Optional.of("/users/" + username);
    }

    @Override
    public Collection<Link> links() {
        Collection<Link> links = new ArrayList<>();
        links.add(new Link("images", "/users/" + username + "/images"));
        return links;
    }

    @Override
    public Map<String, Object> embedded() {
        return Collections.emptyMap();
    }

}

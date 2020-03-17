package it.polimi.rest.models;

import com.google.gson.annotations.Expose;
import it.polimi.rest.authorization.AuthorizationProxy;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.authorization.Permission;

import java.util.*;

public class User implements Model {

    @Expose(deserialize = false)
    public final UserId id;

    @Expose
    public final String username;

    @Expose(serialize = false)
    public final String password;

    public User(UserId id, String username, String password) {
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
    public Map<String, Link> links() {
        Map<String, Link> links = new HashMap<>();
        images().ifPresent(url -> links.put("images", new Link(url)));
        return links;
    }

    @Override
    public Map<String, Object> embedded() {
        return Collections.emptyMap();
    }

    public Optional<String> images() {
        return self().map(url -> url + "/images");
    }

}

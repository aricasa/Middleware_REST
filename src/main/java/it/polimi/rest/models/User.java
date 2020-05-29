package it.polimi.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import it.polimi.rest.authorization.Agent;
import it.polimi.rest.authorization.SecuredObject;

import java.util.*;

public class User implements Model {

    @Expose(deserialize = false)
    public final User.Id id;

    @Expose
    public String username;

    @Expose(serialize = false)
    public String password;

    public User(User.Id id, String username, String password) {
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
        return Optional.of(baseUrl() + "/users/" + username);
    }

    @Override
    public Map<String, Link> links() {
        Map<String, Link> links = new HashMap<>();
        images().ifPresent(url -> links.put("images", new Link(url)));
        oAuth2Clients().ifPresent(url -> links.put("oauth2_clients", new Link(url)));
        return links;
    }

    @Override
    public Map<String, Object> embedded() {
        return Collections.emptyMap();
    }

    public Optional<String> images() {
        return self().map(url -> url + "/images");
    }

    public Optional<String> oAuth2Clients() {
        return self().map(url -> url + "/oauth2/clients");
    }

    @JsonAdapter(Id.Adapter.class)
    public static class Id extends it.polimi.rest.models.Id implements Agent, SecuredObject {

        public Id(String id) {
            super(id);
        }

    }

}

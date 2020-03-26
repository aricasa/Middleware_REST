package it.polimi.rest.models.oauth2;

import com.google.gson.annotations.Expose;
import it.polimi.rest.models.Link;
import it.polimi.rest.models.Model;
import it.polimi.rest.models.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OAuth2Client implements Model {

    public final User owner;

    @Expose(deserialize = false)
    public final OAuth2ClientId id;

    @Expose(deserialize = false)
    public final OAuth2ClientSecret secret;

    @Expose
    public final String name;

    @Expose
    public final String callback;

    public OAuth2Client(User owner,
                        OAuth2ClientId id,
                        OAuth2ClientSecret secret,
                        String name,
                        String callback) {

        this.owner = owner;
        this.id = id;
        this.secret = secret;
        this.name = name;
        this.callback = callback;
    }

    @Override
    public String toString() {
        return id.toString();
    }

    @Override
    public Optional<String> self() {
        return OAuth2ClientsList.placeholder(owner).self().map(url -> url + "/" + id);
    }

    @Override
    public Map<String, Link> links() {
        Map<String, Link> links = new HashMap<>();
        owner.self().ifPresent(url -> links.put("author", new Link(url)));
        return links;
    }

    @Override
    public Map<String, Object> embedded() {
        return Collections.emptyMap();
    }

}

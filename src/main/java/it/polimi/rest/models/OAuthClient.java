package it.polimi.rest.models;

import com.google.gson.annotations.Expose;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class OAuthClient implements Model {

    public final UserId owner;

    @Expose
    public final OAuthClientId id;

    @Expose
    public final OAuthClientSecret secret;

    @Expose
    public final String name;

    @Expose
    public final String callback;

    public OAuthClient(UserId owner,
                       OAuthClientId id,
                       OAuthClientSecret secret,
                       String name,
                       String callback) {

        this.owner = owner;
        this.id = id;
        this.secret = secret;
        this.name = name;
        this.callback = callback;
    }

    @Override
    public Optional<String> self() {
        return Optional.empty();
    }

    @Override
    public Map<String, Link> links() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> embedded() {
        return Collections.emptyMap();
    }
}

package it.polimi.rest.models;

import com.google.gson.annotations.Expose;

import java.util.*;

public class ImageMetadata implements Model, TokenAcceptor {

    @Expose
    public final ImageId id;

    @Expose
    public final String title;

    public final User owner;

    public ImageMetadata(ImageId id, String title, User owner) {
        this.id = id;
        this.title = title;
        this.owner = owner;
    }

    @Override
    public Optional<String> self() {
        return owner.images().map(url -> url + "/" + id);
    }

    @Override
    public Map<String, Link> links() {
        Map<String, Link> links = new HashMap<>();
        owner.self().ifPresent(url -> links.put("author", new Link(url)));
        raw().ifPresent(url -> links.put("describes", new Link(url)));
        return links;
    }

    @Override
    public Map<String, Object> embedded() {
        return Collections.emptyMap();
    }

    @Override
    public boolean accept(Token token) {
        return token.managed.equals(owner.id);
    }

    public Optional<String> raw() {
        return self().map(url -> url + "/raw");
    }

}

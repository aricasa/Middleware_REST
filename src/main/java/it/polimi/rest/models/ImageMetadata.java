package it.polimi.rest.models;

import com.google.gson.annotations.Expose;
import it.polimi.rest.messages.Link;

import java.util.*;

public class ImageMetadata implements Model {

    @Expose
    public final String id;

    @Expose
    public final String title;

    public final User owner;

    public ImageMetadata(String id, String title, User owner) {
        this.id = id;
        this.title = title;
        this.owner = owner;
    }

    @Override
    public Optional<String> self() {
        return Optional.of("/users/" + owner.username + "/images/" + id);
    }

    @Override
    public Collection<Link> links() {
        Collection<Link> links = new ArrayList<>();
        links.add(new Link("owner", "/user/" + owner.username));
        links.add(new Link("raw", "/user/" + owner.username + "/images/" + id + "/raw"));
        return links;
    }

    @Override
    public Map<String, Object> embedded() {
        return Collections.emptyMap();
    }

}

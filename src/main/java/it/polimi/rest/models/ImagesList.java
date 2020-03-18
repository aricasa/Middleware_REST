package it.polimi.rest.models;

import com.google.gson.annotations.Expose;

import java.util.*;

public class ImagesList implements Model {

    public final User owner;
    private final Collection<ImageMetadata> images;

    @Expose
    public final int count;

    public ImagesList(User owner, Collection<ImageMetadata> images) {
        this.owner = owner;
        this.images = images;
        this.count = images.size();
    }

    @Override
    public Optional<String> self() {
        return owner.images();
    }

    @Override
    public Map<String, Link> links() {
        Map<String, Link> links = new HashMap<>();
        owner.self().ifPresent(url -> links.put("author", new Link(url)));
        return links;
    }

    @Override
    public Map<String, Object> embedded() {
        return Collections.singletonMap("item", images);
    }

}

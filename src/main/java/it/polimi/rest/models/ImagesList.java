package it.polimi.rest.models;

import it.polimi.rest.messages.Link;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class ImagesList implements Model {

    private final User owner;
    private final Collection<ImageMetadata> images;

    public ImagesList(User owner, Collection<ImageMetadata> images) {
        this.owner = owner;
        this.images = images;
    }

    @Override
    public Optional<String> self() {
        return Optional.of("/users/" + owner.username + "/images");
    }

    @Override
    public Collection<Link> links() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, Object> embedded() {
        return Collections.singletonMap("images", images);
    }

}

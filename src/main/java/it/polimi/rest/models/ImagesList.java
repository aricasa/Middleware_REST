package it.polimi.rest.models;

import com.google.gson.annotations.Expose;
import it.polimi.rest.authorization.SecuredObject;

import java.util.*;
import java.util.function.Consumer;

public class ImagesList implements Model, SecuredObject, Iterable<ImageMetadata> {

    public final User owner;
    private final Collection<ImageMetadata> images;

    @Expose
    public final int count;

    public ImagesList(User owner, Collection<ImageMetadata> images) {
        this.owner = owner;
        this.images = images;
        this.count = images.size();
    }

    public static ImagesList placeholder(User user) {
        return new ImagesList(user, Collections.emptyList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImagesList that = (ImagesList) o;
        return owner.id.equals(that.owner.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner.id);
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

    @Override
    public Iterator<ImageMetadata> iterator() {
        return images.iterator();
    }

    @Override
    public void forEach(Consumer<? super ImageMetadata> action) {
        images.forEach(action);
    }

    @Override
    public Spliterator<ImageMetadata> spliterator() {
        return images.spliterator();
    }

}

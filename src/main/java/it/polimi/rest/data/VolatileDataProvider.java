package it.polimi.rest.data;

import it.polimi.rest.exceptions.NotFoundException;
import it.polimi.rest.models.*;

import java.util.*;

import static java.util.UUID.randomUUID;

public class VolatileDataProvider implements DataProvider {

    private final Map<ImageId, Image> images = new HashMap<>();
    private final Map<UserId, Collection<ImageMetadata>> userImages = new HashMap<>();
    private final Collection<ImageId> reserved = new HashSet<>();

    @Override
    public boolean contains(ImageId id) {
        return images.containsKey(id);
    }

    @Override
    public synchronized ImageId getUniqueId() {
        ImageId id;

        do {
            id = new ImageId(randomUUID().toString().split("-")[0]);
        } while (contains(id) || reserved.contains(id));

        // Reserve the ID
        reserved.add(id);

        return id;
    }

    @Override
    public Image image(ImageId id) {
        if (!images.containsKey(id)) {
            throw new NotFoundException();
        }

        return images.get(id);
    }

    @Override
    public ImagesList images(UserId user) {
        return new ImagesList(user, userImages.getOrDefault(user, Collections.emptyList()));
    }

    @Override
    public void add(Image image) {
        images.put(image.info.id, image);

        if (!userImages.containsKey(image.info.owner)) {
            userImages.put(image.info.owner, new HashSet<>());
        }

        userImages.get(image.info.owner).add(image.info);
        reserved.remove(image.info.id);
    }

    @Override
    public void remove(ImageId id) {
        Image image = images.get(id);
        images.remove(id);
        userImages.getOrDefault(image.info.owner, Collections.emptySet()).remove(image.info);
    }

}

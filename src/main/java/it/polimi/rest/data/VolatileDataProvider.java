package it.polimi.rest.data;

import it.polimi.rest.models.Image;
import it.polimi.rest.models.ImageMetadata;
import it.polimi.rest.models.User;

import java.util.*;

public class VolatileDataProvider implements DataProvider {

    private final Map<String, Image> images = new HashMap<>();
    private final Map<User, Collection<ImageMetadata>> userImages = new HashMap<>();

    @Override
    public boolean contains(String id) {
        return images.containsKey(id);
    }

    @Override
    public Optional<Image> get(String id) {
        return Optional.ofNullable(images.get(id));
    }

    @Override
    public Collection<ImageMetadata> get(User user) {
        return userImages.getOrDefault(user, Collections.emptyList());
    }

    @Override
    public void put(Image image) {
        images.put(image.info.id, image);

        if (!userImages.containsKey(image.info.owner)) {
            userImages.put(image.info.owner, new HashSet<>());
        }

        userImages.get(image.info.owner).add(image.info);
    }

    @Override
    public void remove(String id) {
        Image image = images.get(id);
        images.remove(id);
        userImages.getOrDefault(image.info.owner, Collections.emptySet()).remove(image.info);
    }

}

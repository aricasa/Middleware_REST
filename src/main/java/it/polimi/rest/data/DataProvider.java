package it.polimi.rest.data;

import it.polimi.rest.models.Image;
import it.polimi.rest.models.ImageMetadata;
import it.polimi.rest.models.User;

import java.util.Collection;
import java.util.Optional;

public interface DataProvider {

    /**
     * Check whether exists an image with a certain ID.
     *
     * @param id    image ID
     * @return true if the ID exists; false otherwise
     */
    boolean contains(String id);

    /**
     * Get an image given its ID
     *
     * @param id    image ID
     * @return image
     */
    Optional<Image> get(String id);

    /**
     * Get all the images of a user.
     *
     * @param user  images owner
     * @return user images
     */
    Collection<ImageMetadata> get(User user);

    /**
     * Add an image.
     *
     * @param image image to be added
     */
    void put(Image image);

    /**
     * Remove an image given its ID.
     *
     * @param id    image ID
     */
    void remove(String id);

}

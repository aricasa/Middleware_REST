package it.polimi.rest.data;

import it.polimi.rest.models.*;

import java.util.Optional;

public interface DataProvider {

    /**
     * Check whether exists an image with a certain ID.
     *
     * @param id    image ID
     * @return true if the ID exists; false otherwise
     */
    boolean contains(ImageId id);

    /**
     * Get a new ID that is guaranteed not to be used by any other image.
     *
     * @return ID
     */
    ImageId getUniqueId();

    /**
     * Get an image given its ID
     *
     * @param id    image ID
     * @return image
     */
    Image image(ImageId id);

    /**
     * Get all the images of a user.
     *
     * @param user  images owner
     * @return user images
     */
    ImagesList images(UserId user);

    /**
     * Add an image.
     *
     * @param image image to be added
     */
    void add(Image image);

    /**
     * Remove an image given its ID.
     *
     * @param id    image ID
     */
    void remove(ImageId id);

}

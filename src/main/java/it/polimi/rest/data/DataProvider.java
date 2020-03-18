package it.polimi.rest.data;

import it.polimi.rest.exceptions.NotFoundException;
import it.polimi.rest.models.*;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface DataProvider {

    /**
     * Reserve and get an ID that is guaranteed not to be used
     * by any other entity.
     *
     * @return unique ID
     */
    <T extends Id> T uniqueId(Function<String, T> supplier);

    /**
     * Get user by ID.
     *
     * @param id    user ID
     * @return user
     */
    User userById(UserId id);

    /**
     * Get user by username.
     *
     * @param username  username
     * @return user
     */
    User userByUsername(String username);

    /**
     * Get all the users.
     *
     * @return users
     */
    UsersList users();

    void add(User user);

    void update(User user);

    void remove(UserId user);

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
     * @param image     image to be removed
     */
    void remove(ImageId image);

}

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

    User userById(UserId id);
    User userByUsername(String username);
    UsersList users();
    void add(User user);
    void update(User user);
    void remove(UserId id);

    Image image(ImageId id);
    ImagesList images(UserId user);
    void add(Image image);
    void remove(ImageId id);

    void add(OAuthClient client);

}

package it.polimi.rest.data;

import it.polimi.rest.exceptions.BadRequestException;
import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.NotFoundException;
import it.polimi.rest.models.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.UUID.randomUUID;

public class VolatileDataProvider implements DataProvider {

    private final Collection<Id> ids = new HashSet<>();
    private final Collection<User> users = new HashSet<>();
    private final Collection<BearerToken> tokens = new HashSet<>();
    private final Collection<Image> images = new HashSet<>();
    private final Collection<OAuthClient> oAuthClients = new HashSet<>();

    @Override
    public synchronized <T extends Id> T uniqueId(Function<String, T> supplier) {
        T id;

        do {
            id = supplier.apply(randomUUID().toString().split("-")[0]);
        } while (ids.contains(id));

        // Reserve the ID
        ids.add(id);

        return id;
    }

    @Override
    public User userById(UserId id) {
        return users.stream()
                .filter(user -> user.id.equals(id))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public User userByUsername(String username) {
        return users.stream()
                .filter(user -> user.username.equals(username))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public UsersList users() {
        return new UsersList(users);
    }

    @Override
    public void add(User user) {
        if (user.username == null) {
            throw new BadRequestException("Username not specified");
        } else if (user.password == null) {
            throw new BadRequestException("Password not specified");
        }

        // ID must be unique
        if (users.stream().anyMatch(u -> u.id.equals(user.id))) {
            throw new ForbiddenException("ID '" + user.id + "' already in use");
        }

        // Username must be unique
        if (users.stream().anyMatch(u -> u.username.equals(user.username))) {
            throw new ForbiddenException("Username '" + user.username + "' already in use");
        }

        users.add(user);
        ids.add(user.id);
    }

    @Override
    public void update(User user) {
        User u = userById(user.id);
        u.username = user.username;
        u.password = user.password;
    }

    @Override
    public void remove(UserId id) {
        if (users.stream().noneMatch(u -> u.id.equals(id))) {
            throw new NotFoundException();
        }

        users.removeIf(user -> user.id.equals(id));
        ids.remove(id);

        // Remove the images of the user
        images.removeIf(image -> image.info.owner.id.equals(id));
    }

    @Override
    public Image image(ImageId id) {
        return images.stream()
                .filter(image -> image.info.id.equals(id))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public ImagesList images(UserId userId) {
        User owner = userById(userId);

        return new ImagesList(owner, images.stream()
                .filter(image -> image.info.owner.id.equals(owner.id))
                .map(image -> image.info)
                .sorted(Comparator.comparing(o -> o.title))
                .collect(Collectors.toList()));
    }

    @Override
    public void add(Image image) {
        if (image.info.title == null) {
            throw new BadRequestException("Title not specified");
        }

        // ID must be unique
        if (images.stream().anyMatch(i -> i.info.id.equals(image.info.id))) {
            throw new ForbiddenException("ID '" + image.info.id + "' already in use");
        }

        images.add(image);
        ids.add(image.info.id);
    }

    @Override
    public void remove(ImageId id) {
        if (images.stream().noneMatch(i -> i.info.id.equals(id))) {
            throw new NotFoundException();
        }

        images.removeIf(image -> image.info.id.equals(id));
        ids.remove(id);
    }

    @Override
    public void add(OAuthClient client) {
        if (client.name == null) {
            throw new BadRequestException("Name not specified");
        } else if (client.callback == null) {
            throw new BadRequestException("Callback URL not specified");
        }

        // Name must be unique
        if (oAuthClients.stream().anyMatch(c -> c.name.equals(client.name))) {
            throw new ForbiddenException("Name '" + client.name + "'already in use");
        }

        oAuthClients.add(client);
    }

}

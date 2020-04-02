package it.polimi.rest.data;

import it.polimi.rest.models.*;
import it.polimi.rest.models.oauth2.OAuth2AccessToken;
import it.polimi.rest.models.oauth2.OAuth2AuthorizationCode;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.OAuth2RefreshToken;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class VolatileStorage implements Storage {

    private final Collection<Id> ids = new HashSet<>();

    private final Collection<User> users = new HashSet<>();
    private final Collection<BasicToken> basicTokens = new HashSet<>();
    private final Collection<Image> images = new HashSet<>();
    private final Collection<OAuth2Client> oAuth2Clients = new HashSet<>();
    private final Collection<OAuth2AuthorizationCode> oAuth2AuthCodes = new HashSet<>();
    private final Collection<OAuth2AccessToken> oAuth2AccessTokens = new HashSet<>();
    private final Collection<OAuth2RefreshToken> oAuth2RefreshTokens = new HashSet<>();

    @Override
    public <T extends Id> T uniqueId(Supplier<String> randomizer, Function<String, T> supplier) {
        T id;

        do {
            id = supplier.apply(randomizer.get());
        } while (ids.contains(id));

        // Reserve the ID
        ids.add(id);

        return id;
    }

    @Override
    public User userById(User.Id id) {
        return users.stream()
                .filter(user -> user.id.equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User userByUsername(String username) {
        return users.stream()
                .filter(user -> user.username.equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Collection<User> users() {
        return Collections.unmodifiableCollection(users);
    }

    @Override
    public void add(User user) {
        users.add(user);
    }

    @Override
    public void update(User user) {
        User u = userById(user.id);
        u.username = user.username;
        u.password = user.password;
    }

    @Override
    public void remove(User.Id id) {
        users.removeIf(user -> user.id.equals(id));
    }

    @Override
    public Collection<BasicToken> basicTokens() {
        return Collections.unmodifiableCollection(basicTokens);
    }

    @Override
    public BasicToken basicToken(BasicToken.Id id) {
        return basicTokens.stream()
                .filter(token -> token.id.equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void add(BasicToken token) {
        basicTokens.add(token);
    }

    @Override
    public void remove(BasicToken.Id id) {
        basicTokens.removeIf(token -> token.id.equals(id));
        ids.remove(id);
    }

    @Override
    public Image image(Image.Id id) {
        return images.stream()
                .filter(image -> image.info.id.equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Collection<ImageMetadata> images(String username) {
        User owner = userByUsername(username);

        return images.stream()
                .filter(image -> image.info.owner.id.equals(owner.id))
                .map(image -> image.info)
                .sorted(Comparator.comparing(o -> o.title))
                .collect(Collectors.toList());
    }

    @Override
    public void add(Image image) {
        images.add(image);
        ids.add(image.info.id);
    }

    @Override
    public void remove(Image.Id id) {
        images.removeIf(image -> image.info.id.equals(id));
        ids.remove(id);
    }

    @Override
    public OAuth2Client oAuth2Client(OAuth2Client.Id id) {
        return oAuth2Clients.stream()
                .filter(client -> client.id.equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Collection<OAuth2Client> oAuth2Clients(User.Id user) {
        User owner = userById(user);

        return oAuth2Clients.stream()
                .filter(client -> client.owner.id.equals(owner.id))
                .sorted(Comparator.comparing(client -> client.name))
                .collect(Collectors.toList());
    }

    @Override
    public void add(OAuth2Client client) {
        oAuth2Clients.add(client);
    }

    @Override
    public void remove(OAuth2Client.Id id) {
        oAuth2Clients.removeIf(client -> client.id.equals(id));
        ids.remove(id);
    }

    @Override
    public OAuth2AuthorizationCode oAuth2AuthCode(OAuth2AuthorizationCode.Id id) {
        return oAuth2AuthCodes.stream()
                .filter(code -> code.id.equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Collection<OAuth2AuthorizationCode> oAuth2AuthorizationCodes() {
        return Collections.unmodifiableCollection(oAuth2AuthCodes);
    }

    @Override
    public void add(OAuth2AuthorizationCode code) {
        oAuth2AuthCodes.add(code);
    }

    @Override
    public void remove(OAuth2AuthorizationCode.Id id) {
        oAuth2AuthCodes.removeIf(code -> code.id.equals(id));
        ids.remove(id);
    }

    @Override
    public OAuth2AccessToken oAuth2AccessToken(OAuth2AccessToken.Id id) {
        return oAuth2AccessTokens.stream()
                .filter(token -> token.id.equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Collection<OAuth2AccessToken> oAuth2AccessTokens() {
        return Collections.unmodifiableCollection(oAuth2AccessTokens);
    }

    @Override
    public void add(OAuth2AccessToken token) {
        oAuth2AccessTokens.add(token);
    }

    @Override
    public void remove(OAuth2AccessToken.Id id) {
        oAuth2AccessTokens.removeIf(token -> token.id.equals(id));
        ids.remove(id);
    }

    @Override
    public OAuth2RefreshToken oAuth2RefreshToken(OAuth2RefreshToken.Id id) {
        return oAuth2RefreshTokens.stream()
                .filter(token -> token.id.equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Collection<OAuth2RefreshToken> oAuth2RefreshTokens() {
        return Collections.unmodifiableCollection(oAuth2RefreshTokens);
    }

    @Override
    public void add(OAuth2RefreshToken token) {
        oAuth2RefreshTokens.add(token);
    }

    @Override
    public void remove(OAuth2RefreshToken.Id id) {
        oAuth2RefreshTokens.removeIf(token -> token.id.equals(id));
    }

}

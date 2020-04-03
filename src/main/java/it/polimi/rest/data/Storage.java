package it.polimi.rest.data;

import it.polimi.rest.exceptions.NotFoundException;
import it.polimi.rest.models.*;
import it.polimi.rest.models.oauth2.*;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Storage {

    /**
     * Reserve and get an ID that is guaranteed not to be used
     * by any other entity.
     *
     * @return unique ID
     */
    <T extends Id> T uniqueId(Supplier<String> randomizer, Function<String, T> supplier);

    User userById(User.Id id);
    User userByUsername(String username);
    Collection<User> users();
    void add(User user);
    void remove(User.Id id);

    Collection<BasicToken> basicTokens();
    BasicToken basicToken(BasicToken.Id id);
    void add(BasicToken token);
    void remove(BasicToken.Id id);

    Image image(Image.Id id);
    Collection<ImageMetadata> images(String username);
    void add(Image image);
    void remove(Image.Id id);

    OAuth2Client oAuth2Client(OAuth2Client.Id id);
    Collection<OAuth2Client> oAuth2Clients(User.Id user);
    void add(OAuth2Client client);
    void remove(OAuth2Client.Id id);

    OAuth2AuthorizationCode oAuth2AuthCode(OAuth2AuthorizationCode.Id id);
    Collection<OAuth2AuthorizationCode> oAuth2AuthorizationCodes();
    void add(OAuth2AuthorizationCode code);
    void remove(OAuth2AuthorizationCode.Id id);

    OAuth2AccessToken oAuth2AccessToken(OAuth2AccessToken.Id id);
    Collection<OAuth2AccessToken> oAuth2AccessTokens();
    void add(OAuth2AccessToken token);
    void remove(OAuth2AccessToken.Id id);

    OAuth2RefreshToken oAuth2RefreshToken(OAuth2RefreshToken.Id id);
    Collection<OAuth2RefreshToken> oAuth2RefreshTokens();
    void add(OAuth2RefreshToken token);
    void remove(OAuth2RefreshToken.Id id);

}

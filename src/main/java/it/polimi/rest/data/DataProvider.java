package it.polimi.rest.data;

import it.polimi.rest.models.*;
import it.polimi.rest.models.oauth2.OAuth2AccessToken;
import it.polimi.rest.models.oauth2.OAuth2AuthorizationCode;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.OAuth2ClientsList;

import java.util.function.Function;
import java.util.function.Supplier;

public interface DataProvider {

    /**
     * Reserve and get an ID that is guaranteed not to be used
     * by any other entity.
     *
     * @return unique ID
     */
    <T extends Id> T uniqueId(Supplier<String> randomizer, Function<String, T> supplier);

    User userById(User.Id id);
    User userByUsername(String username);
    UsersList users();
    void add(User user);
    void update(User user);
    void remove(User.Id id);

    Image image(Image.Id id);
    ImagesList images(String username);
    void add(Image image);
    void remove(Image.Id id);

    OAuth2Client oAuth2Client(OAuth2Client.Id id);
    OAuth2ClientsList oAuth2Clients(User.Id user);
    void add(OAuth2Client client);
    void remove(OAuth2Client.Id id);

    OAuth2AuthorizationCode oAuth2AuthCode(OAuth2AuthorizationCode.Id id);
    void add(OAuth2AuthorizationCode code);
    void remove(OAuth2AuthorizationCode.Id id);

    OAuth2AccessToken oAuth2AccessToken(OAuth2AccessToken.Id id);
    void add(OAuth2AccessToken token);
    void remove(OAuth2AccessToken.Id id);

}

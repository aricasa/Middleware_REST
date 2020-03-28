package it.polimi.rest.data;

import it.polimi.rest.models.*;
import it.polimi.rest.models.oauth2.OAuth2AuthorizationCode;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.OAuth2ClientId;
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

    OAuth2Client oAuth2Client(OAuth2ClientId id);
    OAuth2ClientsList oAuth2Clients(UserId user);
    void add(OAuth2Client client);
    void remove(OAuth2ClientId id);

    OAuth2AuthorizationCode oAuth2AuthCode(OAuth2AuthorizationCode.OAuth2AuthorizationCodeId id);
    void add(OAuth2AuthorizationCode code);
    void remove(OAuth2AuthorizationCode.OAuth2AuthorizationCodeId id);

}

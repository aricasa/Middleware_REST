package it.polimi.rest.data;

import it.polimi.rest.exceptions.BadRequestException;
import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.NotFoundException;
import it.polimi.rest.models.*;
import it.polimi.rest.models.oauth2.*;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface DataProvider {

    <T extends Id> T uniqueId(Supplier<String> randomizer, Function<String, T> supplier);
    User userById(User.Id id);
    User userByUsername(String username);
    UsersList users();
    void add(User user);
    void remove(User.Id id);
    BasicToken basicToken(BasicToken.Id id);
    void add(BasicToken token);
    void remove(BasicToken.Id id);
    Image image(Image.Id imageId);
    ImagesList images(String username);
    void add(Image image);
    void remove(Image.Id imageId);
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
    OAuth2RefreshToken oAuth2RefreshToken(OAuth2RefreshToken.Id id);
    void add(OAuth2RefreshToken token);
    void remove(OAuth2RefreshToken.Id id);

}

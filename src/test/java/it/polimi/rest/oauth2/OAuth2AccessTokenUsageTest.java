package it.polimi.rest.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.*;
import it.polimi.rest.models.Image;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2AccessToken;
import it.polimi.rest.models.oauth2.OAuth2AuthorizationCode;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.scope.Scope;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import static it.polimi.rest.models.oauth2.scope.Scope.READ_IMAGES;
import static it.polimi.rest.models.oauth2.scope.Scope.READ_USER;
import static org.junit.Assert.assertEquals;

public class OAuth2AccessTokenUsageTest extends OAuth2AbstractTest {

    private String username = "user";
    private String password = "pass";
    private TokenId token;
    private Image.Id imageId;
    private String callback = "http://localhost/callback";
    private OAuth2Client.Id clientId;
    private OAuth2Client.Secret clientSecret;

    @Before
    public void setUp() throws Exception {
        addUser(username, password);
        token = new TokenId(login(username, password).id);

        File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("image.jpg")).getFile());
        imageId = new Image.Id(addImage(token, username, "title", file).id);

        OAuth2ClientAddMessage.Response response = addClient(token, username, "client", callback);
        clientId = new OAuth2Client.Id(response.id);
        clientSecret = new OAuth2Client.Secret(response.secret);
    }

    private OAuth2AccessToken.Id accessToken(String username, String password, Collection<Scope> scopes) throws IOException {
        TokenId token = new TokenId(login(username, password).id);

        OAuth2GrantMessage.Response grantResponse = authCode(token, clientId, callback, scopes, null);
        OAuth2AuthorizationCode.Id authCode = new OAuth2AuthorizationCode.Id(grantResponse.authorizationCode);

        return new OAuth2AccessToken.Id(accessToken(clientId, clientSecret, callback, authCode).access_token);
    }

    @Test
    public void userInfoWithRightPrivileges() throws Exception {
        Collection<Scope> scopes = Collections.singletonList(Scope.get(READ_USER));
        OAuth2AccessToken.Id accessToken = accessToken(username, password, scopes);

        RootMessage.Response rootLinks = rootLinks();
        UserInfoMessage.Request request = new UserInfoMessage.Request(rootLinks, accessToken, username);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.OK, response.getStatusLine().getStatusCode());
    }

    @Test
    public void userInfoWithWrongPrivileges() throws Exception {
        Collection<Scope> scopes = Collections.singletonList(Scope.get(READ_IMAGES));
        OAuth2AccessToken.Id accessToken = accessToken(username, password, scopes);

        RootMessage.Response rootLinks = rootLinks();
        UserInfoMessage.Request request = new UserInfoMessage.Request(rootLinks, accessToken, username);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

    @Test
    public void imagesListWithRightPrivileges() throws Exception {
        Collection<Scope> scopes = Collections.singletonList(Scope.get(READ_IMAGES));
        OAuth2AccessToken.Id accessToken = accessToken(username, password, scopes);

        UserInfoMessage.Response userInfo = userInfo(token, username);
        ImagesListMessage.Request request = new ImagesListMessage.Request(userInfo, accessToken);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.OK, response.getStatusLine().getStatusCode());
    }

    @Test
    public void imagesListWithWrongPrivileges() throws Exception {
        Collection<Scope> scopes = Collections.singletonList(Scope.get(READ_USER));
        OAuth2AccessToken.Id accessToken = accessToken(username, password, scopes);

        UserInfoMessage.Response userInfo = userInfo(token, username);
        ImagesListMessage.Request request = new ImagesListMessage.Request(userInfo, accessToken);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

    @Test
    public void imageInfoWithRightPrivileges() throws Exception {
        Collection<Scope> scopes = Collections.singletonList(Scope.get(READ_IMAGES));
        OAuth2AccessToken.Id accessToken = accessToken(username, password, scopes);

        UserInfoMessage.Response userInfo = userInfo(token, username);
        ImageInfoMessage.Request request = new ImageInfoMessage.Request(userInfo, accessToken, imageId);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.OK, response.getStatusLine().getStatusCode());
    }

    @Test
    public void imageInfoWithWrongPrivileges() throws Exception {
        Collection<Scope> scopes = Collections.singletonList(Scope.get(READ_USER));
        OAuth2AccessToken.Id accessToken = accessToken(username, password, scopes);

        UserInfoMessage.Response userInfo = userInfo(token, username);
        ImageInfoMessage.Request request = new ImageInfoMessage.Request(userInfo, accessToken, imageId);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}
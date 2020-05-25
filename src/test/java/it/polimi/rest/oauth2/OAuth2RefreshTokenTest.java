package it.polimi.rest.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.*;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2AccessToken;
import it.polimi.rest.models.oauth2.OAuth2AuthorizationCode;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.OAuth2RefreshToken;
import it.polimi.rest.models.oauth2.scope.Scope;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class OAuth2RefreshTokenTest extends OAuth2AbstractTest {

    private String username = "user";
    private OAuth2Client.Id clientId;
    private OAuth2Client.Secret clientSecret;
    private OAuth2AccessToken.Id accessToken;
    private OAuth2RefreshToken.Id refreshToken;

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        TokenId token = new TokenId(login(username, "pass").id);

        String callback = "http://localhost/callback";
        OAuth2ClientAddMessage.Response clientAddResponse = addClient(token, username, "client", callback);
        clientId = new OAuth2Client.Id(clientAddResponse.id);
        clientSecret = new OAuth2Client.Secret(clientAddResponse.secret);

        List<Scope> scopes = Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES));
        OAuth2GrantMessage.Response grantResponse = authCode(token, clientId, callback, scopes, "state");
        OAuth2AuthorizationCode.Id authCode = new OAuth2AuthorizationCode.Id(grantResponse.authorizationCode);

        OAuth2AccessTokenMessage.Response response = new OAuth2AccessTokenMessage.Request(clientId, clientSecret, callback, authCode, "authorization_code").response(BASE_URL);
        accessToken = new OAuth2AccessToken.Id(response.access_token);
        refreshToken = new OAuth2RefreshToken.Id(response.refresh_token);
    }

    @Test
    public void response() throws Exception {
        HttpResponse response = new OAuth2RefreshTokenMessage.Request(clientId, clientSecret, "refresh_token", refreshToken).rawResponse(BASE_URL);
        assertEquals(HttpStatus.CREATED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void newValidAccessToken() throws Exception {
        OAuth2RefreshTokenMessage.Response response = refreshToken(clientId, clientSecret, refreshToken);
        assertNotNull(response.access_token);
    }

    @Test
    public void newDifferentAccessToken() throws Exception {
        OAuth2RefreshTokenMessage.Response response = refreshToken(clientId, clientSecret, refreshToken);
        assertNotEquals(accessToken, response.access_token);
    }

    @Test
    public void validExpiration() throws Exception {
        OAuth2RefreshTokenMessage.Response response = refreshToken(clientId, clientSecret, refreshToken);
        assertNotNull(response.expires_in);
    }

    @Test
    public void newValidRefreshToken() throws Exception {
        OAuth2RefreshTokenMessage.Response response = refreshToken(clientId, clientSecret, refreshToken);
        assertNotNull(response.refresh_token);
    }

    @Test
    public void newDifferentRefreshToken() throws Exception {
        OAuth2RefreshTokenMessage.Response response = refreshToken(clientId, clientSecret, refreshToken);
        assertNotEquals(refreshToken, response.refresh_token);
    }

    @Test
    public void oldAccessTokenNotValidAnymore() throws Exception {
        refreshToken(clientId, clientSecret, refreshToken);

        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Request request = new UserInfoMessage.Request(rootLinks, new TokenId(accessToken.toString()), "user");
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void newAccessTokenIsWorking() throws Exception {
        OAuth2RefreshTokenMessage.Response response = new OAuth2RefreshTokenMessage.Request(clientId, clientSecret,  "refresh_token", refreshToken).response(BASE_URL);

        String newAccessToken = response.access_token;
        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Request request = new UserInfoMessage.Request(rootLinks, new TokenId(newAccessToken), username);

        assertEquals(HttpStatus.OK, request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void invalidRefreshToken() throws Exception {
        OAuth2RefreshToken.Id invalidRefreshToken = new OAuth2RefreshToken.Id(refreshToken + "invalidToken");

        OAuth2RefreshTokenMessage.Request request = new OAuth2RefreshTokenMessage.Request(clientId, clientSecret, "refresh_token", invalidRefreshToken);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void missingClientId() throws Exception {
        OAuth2RefreshTokenMessage.Request request = new OAuth2RefreshTokenMessage.Request(null, clientSecret, "refresh_token", refreshToken);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void wrongClientId() throws Exception {
        OAuth2Client.Id wrongClientId = new OAuth2Client.Id(clientId + "wrongId");

        OAuth2RefreshTokenMessage.Request request = new OAuth2RefreshTokenMessage.Request(wrongClientId, clientSecret, "refresh_token", refreshToken);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void missingClientSecret() throws Exception {
        OAuth2RefreshTokenMessage.Request request = new OAuth2RefreshTokenMessage.Request(clientId, null, "refresh_token", refreshToken);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void wrongClientSecret() throws Exception {
        OAuth2Client.Secret wrongClientSecret = new OAuth2Client.Secret(clientSecret + "wrongSecret");

        OAuth2RefreshTokenMessage.Request request = new OAuth2RefreshTokenMessage.Request(clientId, wrongClientSecret, "refresh_token", refreshToken);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

}
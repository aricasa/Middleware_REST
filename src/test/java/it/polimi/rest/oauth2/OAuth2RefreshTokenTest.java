package it.polimi.rest.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.*;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.scope.Scope;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class OAuth2RefreshTokenTest extends OAuth2AbstractTest {

    private TokenId token;
    private OAuth2Client.Id clientId;
    private OAuth2Client.Secret clientSecret;
    private String authorizationCode;
    private String accessToken;
    private String refreshToken;
    private String callback = "http://localhost/callback";

    @Before
    public void setUp() throws Exception {
        addUser("user", "pass");
        token = new TokenId(login("user", "pass").id);

        OAuth2ClientAdd.Response clientAddResponse = addClient(token, "user", "client", callback);
        clientId = new OAuth2Client.Id(clientAddResponse.id);
        clientSecret = new OAuth2Client.Secret(clientAddResponse.secret);

        List<Scope> scopes = Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES));
        OAuth2Grant.Response grantResponse = authCode(token, clientId, callback, scopes, "state");

        authorizationCode = grantResponse.authorizationCode;
        OAuth2AccessToken.Response response = new OAuth2AccessToken.Request(clientId, clientSecret, callback, authorizationCode, "authorization_code").response(BASE_URL);
        accessToken = response.access_token;
        refreshToken = response.refresh_token;
    }

    @Test
    public void valid() throws Exception {
        HttpResponse response = new OAuth2RefreshToken.Request(clientId, clientSecret, "refresh_token", refreshToken).rawResponse(BASE_URL);
        assertEquals(HttpStatus.CREATED,response.getStatusLine().getStatusCode());
    }

    @Test
    public void validAccessToken() throws Exception {
        OAuth2RefreshToken.Response response = new OAuth2RefreshToken.Request(clientId, clientSecret,  "refresh_token", refreshToken).response(BASE_URL);
        assertNotNull(response.access_token);
        assertTrue(response.expires_in!=null);
        assertTrue(response.refresh_token!=null);
        assertTrue(response.token_type.compareTo("bearer")==0);
    }

    @Test
    public void validExpiresTime() throws Exception {
        OAuth2RefreshToken.Response response = new OAuth2RefreshToken.Request(clientId, clientSecret,  "refresh_token", refreshToken).response(BASE_URL);
        assertNotNull(response.expires_in);
    }

    @Test
    public void validRefreshToken() throws Exception {
        OAuth2RefreshToken.Response response = new OAuth2RefreshToken.Request(clientId, clientSecret,  "refresh_token", refreshToken).response(BASE_URL);
        assertNotNull(response.refresh_token);
    }

    @Test
    public void validTokenType() throws Exception {
        OAuth2RefreshToken.Response response = new OAuth2RefreshToken.Request(clientId, clientSecret,  "refresh_token", refreshToken).response(BASE_URL);
        assertEquals(response.token_type,"bearer");
    }

    @Test
    public void checkOldTokenNoMoreValid() throws Exception {
        OAuth2RefreshToken.Response response = new OAuth2RefreshToken.Request(clientId, clientSecret,  "refresh_token", refreshToken).response(BASE_URL);
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UserInfo.Request request = new UserInfo.Request(rootLinks, new TokenId(accessToken), "user");
        assertEquals(HttpStatus.UNAUTHORIZED, request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void checkNewTokenIsValid() throws Exception {
        OAuth2RefreshToken.Response response = new OAuth2RefreshToken.Request(clientId, clientSecret,  "refresh_token", refreshToken).response(BASE_URL);
        String newAccessToken = response.access_token;
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UserInfo.Request request = new UserInfo.Request(rootLinks, new TokenId(newAccessToken), "user");
        assertEquals(HttpStatus.OK, request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void incorrectRefreshToken() throws Exception {
        HttpResponse response = new OAuth2RefreshToken.Request(clientId, clientSecret, "refresh_token", "fakeToken").rawResponse(BASE_URL);
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusLine().getStatusCode());
    }

    @Test
    public void incorrectClientSecret() throws Exception {
        HttpResponse response = new OAuth2RefreshToken.Request(clientId, new OAuth2Client.Secret("fakeSecret"), "refresh_token", refreshToken).rawResponse(BASE_URL);
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusLine().getStatusCode());
    }

    @Test
    public void incorrectClientId() throws Exception {
        HttpResponse response = new OAuth2RefreshToken.Request(new OAuth2Client.Id("fakeClientID"), clientSecret, "refresh_token", refreshToken).rawResponse(BASE_URL);
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusLine().getStatusCode());
    }

    @Test
    public void missingClientSecret() throws Exception {
        HttpResponse response = new OAuth2RefreshToken.Request(clientId, null, "refresh_token", refreshToken).rawResponse(BASE_URL);
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusLine().getStatusCode());
    }

    @Test
    public void missingClientId() throws Exception {
        HttpResponse response = new OAuth2RefreshToken.Request(null, clientSecret, "refresh_token", refreshToken).rawResponse(BASE_URL);
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusLine().getStatusCode());
    }

}
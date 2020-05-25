package it.polimi.rest.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.OAuth2AccessToken;
import it.polimi.rest.messages.OAuth2ClientAdd;
import it.polimi.rest.messages.OAuth2Grant;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.scope.Scope;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class OAuth2AccessTokenTest extends OAuth2AbstractTest {

    private TokenId token;
    private OAuth2Client.Id clientId;
    private OAuth2Client.Secret clientSecret;
    private String authorizationCode;
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
    }

    @Test
    public void valid() throws Exception {
        OAuth2AccessToken.Request request = new OAuth2AccessToken.Request(clientId, clientSecret, callback, authorizationCode, "authorization_code");
        assertEquals(HttpStatus.CREATED,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void validRefreshTokenRetrieved() throws Exception {
        OAuth2AccessToken.Response response = new OAuth2AccessToken.Request(clientId, clientSecret, callback, authorizationCode, "authorization_code").response(BASE_URL);
        assertNotNull(response.refresh_token);
    }

    @Test
    public void validExpireTimeRetrieved() throws Exception {
        OAuth2AccessToken.Response response = new OAuth2AccessToken.Request(clientId, clientSecret, callback, authorizationCode, "authorization_code").response(BASE_URL);
        assertNotNull(response.expires_in);
    }

    @Test
    public void validTokenTypeRetrieved() throws Exception {
        OAuth2AccessToken.Response response = new OAuth2AccessToken.Request(clientId, clientSecret, callback, authorizationCode, "authorization_code").response(BASE_URL);
        assertEquals(response.token_type, "bearer");
    }

    @Test
    public void validAccessTokenRetrieved() throws Exception {
        OAuth2AccessToken.Response response = new OAuth2AccessToken.Request(clientId, clientSecret, callback, authorizationCode, "authorization_code").response(BASE_URL);
        assertNotNull(response.access_token);
    }

    @Test
    public void incorrectAuthorizationCode() throws Exception {
        OAuth2AccessToken.Request request = new OAuth2AccessToken.Request(clientId, clientSecret, callback, "fakeCode", "authorization_code");
        assertEquals(HttpStatus.BAD_REQUEST,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void incorrectClientId() throws Exception {
        OAuth2AccessToken.Request request = new OAuth2AccessToken.Request(new OAuth2Client.Id("fakeClient"), clientSecret, callback, authorizationCode, "authorization_code");
        assertEquals(HttpStatus.BAD_REQUEST,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void mismatchingCallbackURL() throws Exception {
        OAuth2AccessToken.Request request = new OAuth2AccessToken.Request(clientId, clientSecret, "wrongURL", authorizationCode, "authorization_code");
        assertEquals(HttpStatus.BAD_REQUEST,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void incorrectClientSecret() throws Exception {
        OAuth2AccessToken.Request request = new OAuth2AccessToken.Request(clientId, new OAuth2Client.Secret("wrongSecret"), callback, authorizationCode, "authorization_code");
        assertEquals(HttpStatus.BAD_REQUEST,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void unknownGrantType() throws Exception {
        OAuth2AccessToken.Request request = new OAuth2AccessToken.Request(clientId, clientSecret, callback, authorizationCode, "fakeGrantType");
        assertEquals(HttpStatus.BAD_REQUEST,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

}
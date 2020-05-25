package it.polimi.rest.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.OAuth2AccessTokenMessage;
import it.polimi.rest.messages.OAuth2ClientAddMessage;
import it.polimi.rest.messages.OAuth2GrantMessage;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2AuthorizationCode;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.scope.Scope;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class OAuth2AccessTokenTest extends OAuth2AbstractTest {

    private OAuth2Client.Id clientId;
    private OAuth2Client.Secret clientSecret;
    private OAuth2AuthorizationCode.Id authorizationCode;
    private String callback = "http://localhost/callback";

    @Before
    public void setUp() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);

        OAuth2ClientAddMessage.Response clientAddResponse = addClient(token, "user", "client", callback);
        clientId = new OAuth2Client.Id(clientAddResponse.id);
        clientSecret = new OAuth2Client.Secret(clientAddResponse.secret);

        List<Scope> scopes = Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES));
        OAuth2GrantMessage.Response grantResponse = authCode(token, clientId, callback, scopes, "state");

        authorizationCode = new OAuth2AuthorizationCode.Id(grantResponse.authorizationCode);
    }

    @Test
    public void valid() throws Exception {
        OAuth2AccessTokenMessage.Request request = new OAuth2AccessTokenMessage.Request(clientId, clientSecret, callback, authorizationCode, "authorization_code");
        assertEquals(HttpStatus.CREATED,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void validRefreshTokenRetrieved() throws Exception {
        OAuth2AccessTokenMessage.Response response = new OAuth2AccessTokenMessage.Request(clientId, clientSecret, callback, authorizationCode, "authorization_code").response(BASE_URL);
        assertNotNull(response.refresh_token);
    }

    @Test
    public void validExpireTimeRetrieved() throws Exception {
        OAuth2AccessTokenMessage.Response response = new OAuth2AccessTokenMessage.Request(clientId, clientSecret, callback, authorizationCode, "authorization_code").response(BASE_URL);
        assertNotNull(response.expires_in);
    }

    @Test
    public void validTokenTypeRetrieved() throws Exception {
        OAuth2AccessTokenMessage.Response response = new OAuth2AccessTokenMessage.Request(clientId, clientSecret, callback, authorizationCode, "authorization_code").response(BASE_URL);
        assertEquals(response.token_type, "bearer");
    }

    @Test
    public void validAccessTokenRetrieved() throws Exception {
        OAuth2AccessTokenMessage.Response response = new OAuth2AccessTokenMessage.Request(clientId, clientSecret, callback, authorizationCode, "authorization_code").response(BASE_URL);
        assertNotNull(response.access_token);
    }

    @Test
    public void invalidAuthorizationCode() throws Exception {
        OAuth2AuthorizationCode.Id invalidCode = new OAuth2AuthorizationCode.Id(authorizationCode + "invalidCode");
        OAuth2AccessTokenMessage.Request request = new OAuth2AccessTokenMessage.Request(clientId, clientSecret, callback, invalidCode, "authorization_code");
        assertEquals(HttpStatus.BAD_REQUEST,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void incorrectClientId() throws Exception {
        OAuth2AccessTokenMessage.Request request = new OAuth2AccessTokenMessage.Request(new OAuth2Client.Id("fakeClient"), clientSecret, callback, authorizationCode, "authorization_code");
        assertEquals(HttpStatus.BAD_REQUEST,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void mismatchingCallbackURL() throws Exception {
        OAuth2AccessTokenMessage.Request request = new OAuth2AccessTokenMessage.Request(clientId, clientSecret, "wrongURL", authorizationCode, "authorization_code");
        assertEquals(HttpStatus.BAD_REQUEST,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void incorrectClientSecret() throws Exception {
        OAuth2AccessTokenMessage.Request request = new OAuth2AccessTokenMessage.Request(clientId, new OAuth2Client.Secret("wrongSecret"), callback, authorizationCode, "authorization_code");
        assertEquals(HttpStatus.BAD_REQUEST,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void unknownGrantType() throws Exception {
        OAuth2AccessTokenMessage.Request request = new OAuth2AccessTokenMessage.Request(clientId, clientSecret, callback, authorizationCode, "fakeGrantType");
        assertEquals(HttpStatus.BAD_REQUEST,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

}
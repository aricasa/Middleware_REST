package it.polimi.rest.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.OAuth2AccessToken;
import it.polimi.rest.messages.OAuth2ClientAdd;
import it.polimi.rest.messages.OAuth2Grant;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.scope.Scope;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;


public class OAuth2AccessTokenTest extends OAuth2AbstractTest
{
    private TokenId token;
    private OAuth2Client.Id clientId;
    private OAuth2Client.Secret clientSecret;
    private String authorizationCode;
    private String callback = "http://localhost/callback";

    @Before
    public void setUp() throws Exception {
        addUser("user", "pass");
        token = new TokenId(login("user", "pass").id);
        OAuth2ClientAdd.Response response = addClient(token, "user", "client", callback);
        clientId = new OAuth2Client.Id(response.id);
        clientSecret = new OAuth2Client.Secret(response.secret);

        //Grant request
        OAuth2Grant.Response responseGrant = authCode(token, clientId, callback,
                Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES)),
                "state");
        authorizationCode = responseGrant.authorizationCode;
    }

    @Test
    public void valid() throws IOException, InterruptedException
    {
        OAuth2AccessToken.Request request = new OAuth2AccessToken.Request(clientId, clientSecret, callback, authorizationCode, "authorization_code");
        assertEquals(HttpStatus.CREATED,request.run(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void incorrectAuthorizationCode() throws IOException, InterruptedException
    {
        OAuth2AccessToken.Request request = new OAuth2AccessToken.Request(clientId, clientSecret, callback, "fakeCode", "authorization_code");
        assertEquals(HttpStatus.BAD_REQUEST,request.run(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void incorrectClientId() throws IOException, InterruptedException
    {
        OAuth2AccessToken.Request request = new OAuth2AccessToken.Request(new OAuth2Client.Id("fakeClient"), clientSecret, callback, authorizationCode, "authorization_code");
        assertEquals(HttpStatus.BAD_REQUEST,request.run(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void mismatchingCallbackURL() throws IOException, InterruptedException
    {
        OAuth2AccessToken.Request request = new OAuth2AccessToken.Request(clientId, clientSecret, "wrongURL", authorizationCode, "authorization_code");
        assertEquals(HttpStatus.BAD_REQUEST,request.run(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void incorrectClientSecret() throws IOException, InterruptedException
    {
        OAuth2AccessToken.Request request = new OAuth2AccessToken.Request(clientId, new OAuth2Client.Secret("wrongSecret"), callback, authorizationCode, "authorization_code");
        assertEquals(HttpStatus.BAD_REQUEST,request.run(BASE_URL).getStatusLine().getStatusCode());
    }


    @Test
    public void unknownGrantType() throws IOException, InterruptedException
    {
        OAuth2AccessToken.Request request = new OAuth2AccessToken.Request(clientId, clientSecret, callback, authorizationCode, "fakeGrantType");
        assertEquals(HttpStatus.BAD_REQUEST,request.run(BASE_URL).getStatusLine().getStatusCode());
    }

}
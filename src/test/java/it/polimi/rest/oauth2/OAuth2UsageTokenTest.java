package it.polimi.rest.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.*;
import it.polimi.rest.models.Image;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.scope.Scope;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;


public class OAuth2UsageTokenTest extends OAuth2AbstractTest
{
    private TokenId token;
    private OAuth2Client.Id clientId;
    private OAuth2Client.Secret clientSecret;
    private String authorizationCode;
    private String callback = "http://localhost/callback";
    private File file = new File(getClass().getClassLoader().getResource("image.jpg").getFile());
    private String accessToken;
    private Image.Id image;

    @Before
    public void setUp() throws Exception {
        addUser("user", "pass");
        token = new TokenId(login("user", "pass").id);
        OAuth2ClientAdd.Response response = addClient(token, "user", "client", callback);
        clientId = new OAuth2Client.Id(response.id);
        clientSecret = new OAuth2Client.Secret(response.secret);
    }

    @Test
    public void getInfoUser() throws IOException, InterruptedException
    {
        //Grant request
        OAuth2Grant.Response responseGrant = authCode(token, clientId, callback,
                Arrays.asList(Scope.get(Scope.READ_USER)),
                "state");
        authorizationCode = responseGrant.authorizationCode;

        //Get access token
        OAuth2AccessToken.Response responseAccessToken = getAccessToken(clientId, clientSecret, callback, authorizationCode, "authorization_code");
        accessToken = responseAccessToken.access_token;

        UserInfo.Request request = new UserInfo.Request(new TokenId(accessToken), "user");
        assertEquals(HttpStatus.OK, request.run(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void getListImages() throws IOException, InterruptedException
    {
        //Grant request
        OAuth2Grant.Response responseGrant = authCode(token, clientId, callback,
                Arrays.asList(Scope.get(Scope.READ_IMAGES)),
                "state");
        authorizationCode = responseGrant.authorizationCode;

        //Get access token
        OAuth2AccessToken.Response responseAccessToken = getAccessToken(clientId, clientSecret, callback, authorizationCode, "authorization_code");
        accessToken = responseAccessToken.access_token;

        ImagesList.Request request = new ImagesList.Request(new TokenId(accessToken), "user");
        assertEquals(HttpStatus.OK, request.run(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void wrongTokenInfoUser() throws IOException, InterruptedException
    {
        //Grant request
        OAuth2Grant.Response responseGrant = authCode(token, clientId, callback,
                Arrays.asList(Scope.get(Scope.READ_USER)),
                "state");
        authorizationCode = responseGrant.authorizationCode;

        //Get access token
        OAuth2AccessToken.Response responseAccessToken = getAccessToken(clientId, clientSecret, callback, authorizationCode, "authorization_code");
        accessToken = responseAccessToken.access_token;

        UserInfo.Request request = new UserInfo.Request(new TokenId("fakeToken"), "user");
        assertEquals(HttpStatus.UNAUTHORIZED, request.run(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void missingTokenInfoUser() throws IOException, InterruptedException
    {
        //Grant request
        OAuth2Grant.Response responseGrant = authCode(token, clientId, callback,
                Arrays.asList(Scope.get(Scope.READ_USER)),
                "state");
        authorizationCode = responseGrant.authorizationCode;

        //Get access token
        OAuth2AccessToken.Response responseAccessToken = getAccessToken(clientId, clientSecret, callback, authorizationCode, "authorization_code");
        accessToken = responseAccessToken.access_token;

        UserInfo.Request request = new UserInfo.Request(null, "user");
        assertEquals(HttpStatus.UNAUTHORIZED, request.run(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void wrongTokenListImages() throws IOException, InterruptedException
    {
        //Grant request
        OAuth2Grant.Response responseGrant = authCode(token, clientId, callback,
                Arrays.asList(Scope.get(Scope.READ_IMAGES), Scope.get(Scope.READ_USER)),
                "state");
        authorizationCode = responseGrant.authorizationCode;

        //Get access token
        OAuth2AccessToken.Response responseAccessToken = getAccessToken(clientId, clientSecret, callback, authorizationCode, "authorization_code");
        accessToken = responseAccessToken.access_token;

        ImagesList.Request request = new ImagesList.Request(new TokenId("fakeToken"), "user");
        assertEquals(HttpStatus.UNAUTHORIZED, request.run(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void missingTokenListImages() throws IOException, InterruptedException
    {
        //Grant request
        OAuth2Grant.Response responseGrant = authCode(token, clientId, callback,
                Arrays.asList(Scope.get(Scope.READ_IMAGES), Scope.get(Scope.READ_USER)),
                "state");
        authorizationCode = responseGrant.authorizationCode;

        //Get access token
        OAuth2AccessToken.Response responseAccessToken = getAccessToken(clientId, clientSecret, callback, authorizationCode, "authorization_code");
        accessToken = responseAccessToken.access_token;

        ImagesList.Request request = new ImagesList.Request(null, "user");
        assertEquals(HttpStatus.UNAUTHORIZED, request.run(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void missingScopeListImages() throws IOException, InterruptedException
    {
        //Grant request
        OAuth2Grant.Response responseGrant = authCode(token, clientId, callback,
                Arrays.asList(Scope.get(Scope.READ_USER)),
                "state");
        authorizationCode = responseGrant.authorizationCode;

        //Get access token
        OAuth2AccessToken.Response responseAccessToken = getAccessToken(clientId, clientSecret, callback, authorizationCode, "authorization_code");
        accessToken = responseAccessToken.access_token;

        ImagesList.Request request = new ImagesList.Request(new TokenId(accessToken), "user");
        assertEquals(HttpStatus.FORBIDDEN, request.run(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void missingScopeInfoUser() throws IOException, InterruptedException
    {
        //Grant request
        OAuth2Grant.Response responseGrant = authCode(token, clientId, callback,
                Arrays.asList(Scope.get(Scope.READ_IMAGES)),
                "state");
        authorizationCode = responseGrant.authorizationCode;

        //Get access token
        OAuth2AccessToken.Response responseAccessToken = getAccessToken(clientId, clientSecret, callback, authorizationCode, "authorization_code");
        accessToken = responseAccessToken.access_token;

        UserInfo.Request request = new UserInfo.Request(new TokenId(accessToken), "user");
        assertEquals(HttpStatus.FORBIDDEN, request.run(BASE_URL).getStatusLine().getStatusCode());
    }

}
package it.polimi.rest.oauth2;

import it.polimi.rest.messages.OAuth2ClientAdd;
import it.polimi.rest.messages.OAuth2Grant;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.scope.Scope;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class OAuth2GrantTest extends OAuth2AbstractTest {

    private OAuth2Client.Id clientId;
    private String callback = "http://localhost/callback";

    @Before
    public void setUp() throws Exception {
        addUser("owner", "pass");
        TokenId token = new TokenId(login("owner", "pass").id);
        OAuth2ClientAdd.Response response = addClient(token, "owner", "client", callback);
        clientId = new OAuth2Client.Id(response.id);
    }

    @Test
    public void response() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);

        OAuth2Grant.Response response = authCode(token, clientId, callback,
                Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES)),
                "state");

        assertNotNull(response.authorizationCode);
        assertFalse(response.authorizationCode.trim().isEmpty());
        assertEquals("state", response.state);
    }

    // TODO: missing token
    // TODO: wrong token
    // TODO: missing client id
    // TODO: wrong client id
    // TODO: missing callback
    // TODO: wrong callback
    // TODO: missing scopes
    // TODO: wrong scopes
    // TODO: missing state

    /*
    @Test
    public void incorrectToken() throws IOException, InterruptedException
    {
        //Grant request
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/grant")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id",clientId)
                .addParameter("response_type","code")
                .addParameter("scope","scope1")
                .addParameter("redirect_uri", callback)
                .addParameter("token","fakeToken")
                .build();

        assertEquals(HttpStatus.UNAUTHORIZED,client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void mismatchingURL() throws IOException, InterruptedException
    {
        //Grant request
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/grant")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id",clientId)
                .addParameter("response_type","code")
                .addParameter("scope","scope1")
                .addParameter("redirect_uri", "differentUrl")
                .addParameter("token",idSession.toString())
                .build();

        assertEquals(HttpStatus.BAD_REQUEST,client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void fakeClient() throws IOException, InterruptedException
    {
        //Grant request
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/grant")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id","myIdd")
                .addParameter("response_type","code")
                .addParameter("scope","scope1")
                .addParameter("redirect_uri", "myUrl")
                .addParameter("token","token")
                .build();

        assertEquals(HttpStatus.NOT_FOUND,client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void missingToken() throws IOException, InterruptedException
    {
        //Grant request
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/grant")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id",clientId)
                .addParameter("response_type","code")
                .addParameter("scope","scope1")
                .addParameter("redirect_uri", callback)
                .build();

        assertEquals(HttpStatus.UNAUTHORIZED,client.execute(request).getStatusLine().getStatusCode());
    }
     */

}